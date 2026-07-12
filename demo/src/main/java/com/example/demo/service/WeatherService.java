package com.example.demo.service;

import com.example.demo.client.ForecastEntry;
import com.example.demo.client.ForecastResponse;
import com.example.demo.client.GeocodingResult;
import com.example.demo.client.OpenWeatherClient;
import com.example.demo.dto.WeatherResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    private static final ZoneOffset KST = ZoneOffset.ofHours(9);
    private static final int FORECAST_HORIZON_DAYS = 5;

    private final OpenWeatherClient openWeatherClient;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public WeatherService(OpenWeatherClient openWeatherClient) {
        this.openWeatherClient = openWeatherClient;
    }

    public WeatherResponse getWeather(String regionName, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        String cacheKey = regionName + "|" + startDate + "|" + endDate;
        CacheEntry cached = cache.get(cacheKey);
        if (cached != null && cached.isFresh()) {
            return cached.response();
        }

        String geocodingQuery = SupportedRegions.toGeocodingQuery(regionName)
                .orElseThrow(() -> new InvalidRegionException("지원하지 않는 지역명: " + regionName));

        GeocodingResult location = openWeatherClient.geocode(geocodingQuery)
                .orElseThrow(() -> new WeatherFetchFailedException("지역 좌표를 찾을 수 없습니다: " + regionName));

        ForecastResponse forecast = openWeatherClient.fetchForecast(location.lat(), location.lon());
        List<ForecastEntry> entries = forecast.list().stream()
                .filter(entry -> isWithinRange(entry, startDate, endDate))
                .toList();

        if (entries.isEmpty()) {
            throw new UnsupportedDateRangeException(
                    "예보 가능 범위(오늘부터 %d일 이내)를 벗어났습니다.".formatted(FORECAST_HORIZON_DAYS));
        }

        WeatherResponse response = toResponse(regionName, startDate, endDate, entries);
        cache.put(cacheKey, new CacheEntry(response, Instant.now()));
        return response;
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException("종료일은 시작일보다 빠를 수 없습니다.");
        }
        if (startDate.isBefore(LocalDate.now(KST))) {
            throw new InvalidDateRangeException("시작일은 오늘 이후여야 합니다.");
        }
    }

    private boolean isWithinRange(ForecastEntry entry, LocalDate startDate, LocalDate endDate) {
        LocalDate entryDate = Instant.ofEpochSecond(entry.dt()).atOffset(KST).toLocalDate();
        return !entryDate.isBefore(startDate) && !entryDate.isAfter(endDate);
    }

    private WeatherResponse toResponse(String regionName, LocalDate startDate, LocalDate endDate, List<ForecastEntry> entries) {
        double tempMin = entries.stream().mapToDouble(e -> e.main().tempMin()).min().orElseThrow();
        double tempMax = entries.stream().mapToDouble(e -> e.main().tempMax()).max().orElseThrow();
        double maxPop = entries.stream().mapToDouble(ForecastEntry::pop).max().orElse(0.0);

        return new WeatherResponse(
                regionName,
                startDate,
                endDate,
                mostFrequentDescription(entries),
                (int) Math.round(tempMin),
                (int) Math.round(tempMax),
                (int) Math.round(maxPop * 100),
                OffsetDateTime.now(KST)
        );
    }

    // 여행 기간 전체를 대표하는 한 줄 요약을 위해, 구간 내 3시간 슬롯 중 가장 많이 등장한 날씨 설명을 사용한다.
    private String mostFrequentDescription(List<ForecastEntry> entries) {
        return entries.stream()
                .map(e -> e.weather().isEmpty() ? "정보 없음" : e.weather().get(0).description())
                .collect(Collectors.groupingBy(d -> d, LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("정보 없음");
    }

    private record CacheEntry(WeatherResponse response, Instant fetchedAt) {
        boolean isFresh() {
            return Instant.now().isBefore(fetchedAt.plus(CACHE_TTL));
        }
    }
}

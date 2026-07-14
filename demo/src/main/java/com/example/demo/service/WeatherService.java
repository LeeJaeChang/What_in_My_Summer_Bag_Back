package com.example.demo.service;

import com.example.demo.client.ForecastEntry;
import com.example.demo.client.ForecastResponse;
import com.example.demo.client.GeocodingResult;
import com.example.demo.client.OpenWeatherClient;
import com.example.demo.dto.WeatherResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

        WeatherResponse response = toWeatherResponse(entries);
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

    private WeatherResponse toWeatherResponse(List<ForecastEntry> entries) {
        double tempMin = entries.stream().mapToDouble(e -> e.main().tempMin()).min().orElseThrow();
        double tempMax = entries.stream().mapToDouble(e -> e.main().tempMax()).max().orElseThrow();
        double avgFeelsLike = entries.stream().mapToDouble(e -> e.main().feelsLike()).average().orElseThrow();
        double maxPop = entries.stream().mapToDouble(ForecastEntry::pop).max().orElse(0.0);

        return new WeatherResponse(
                roundToOneDecimal(tempMin),
                roundToOneDecimal(tempMax),
                roundToOneDecimal(avgFeelsLike),
                (int) Math.round(maxPop * 100)
        );
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10) / 10.0;
    }

    private record CacheEntry(WeatherResponse response, Instant fetchedAt) {
        boolean isFresh() {
            return Instant.now().isBefore(fetchedAt.plus(CACHE_TTL));
        }
    }
}

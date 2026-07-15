package com.example.demo.service;

import com.example.demo.client.ForecastEntry;
import com.example.demo.client.ForecastResponse;
import com.example.demo.client.GeocodingResult;
import com.example.demo.client.HistoricalDataPoint;
import com.example.demo.client.OpenWeatherClient;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.SupportedRegion;
import com.example.demo.icon.TdsWeatherIcon;
import com.example.demo.repository.SupportedRegionRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    private static final ZoneOffset KST = ZoneOffset.ofHours(9);
    private static final int FORECAST_HORIZON_DAYS = 5;
    private static final int HISTORICAL_YEARS_BACK = 1;

    private final OpenWeatherClient openWeatherClient;
    private final SupportedRegionRepository supportedRegionRepository;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public WeatherService(OpenWeatherClient openWeatherClient,
                          SupportedRegionRepository supportedRegionRepository) {
        this.openWeatherClient = openWeatherClient;
        this.supportedRegionRepository = supportedRegionRepository;
    }

    public WeatherResponse getWeather(String regionName, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        String cacheKey = regionName + "|" + startDate + "|" + endDate;
        CacheEntry cached = cache.get(cacheKey);
        if (cached != null && cached.isFresh()) {
            return cached.response();
        }

        GeocodingResult location = resolveLocation(regionName);

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

    // 예보 가능 범위(FORECAST_HORIZON_DAYS)를 벗어난 여행에 쓴다: 작년 같은 기간의 실측 날씨로 AI 추천을 근사한다.
    public WeatherResponse getLastYearWeather(String regionName, LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException("종료일은 시작일보다 빠를 수 없습니다.");
        }

        String cacheKey = "lastYear|" + regionName + "|" + startDate + "|" + endDate;
        CacheEntry cached = cache.get(cacheKey);
        if (cached != null && cached.isFresh()) {
            return cached.response();
        }

        GeocodingResult location = resolveLocation(regionName);

        List<HistoricalDataPoint> points = startDate.datesUntil(endDate.plusDays(1))
                .map(date -> date.minusYears(HISTORICAL_YEARS_BACK))
                .map(lastYearDate -> fetchHistoricalDataPoint(location, lastYearDate))
                .toList();

        WeatherResponse response = toLastYearWeatherResponse(points);
        cache.put(cacheKey, new CacheEntry(response, Instant.now()));
        return response;
    }

    private HistoricalDataPoint fetchHistoricalDataPoint(GeocodingResult location, LocalDate date) {
        long start = date.atTime(12, 0).atOffset(KST).toEpochSecond();
        return openWeatherClient.fetchHistoricalWeather(location.lat(), location.lon(), start).data().get(0);
    }

    private GeocodingResult resolveLocation(String regionName) {
        String geocodingQuery = supportedRegionRepository.findById(regionName)
                .map(SupportedRegion::getGeocodingQuery)
                .orElseThrow(() -> new InvalidRegionException("지원하지 않는 지역명: " + regionName));

        return openWeatherClient.geocode(geocodingQuery)
                .orElseThrow(() -> new WeatherFetchFailedException("지역 좌표를 찾을 수 없습니다: " + regionName));
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
        String weatherIconKey = toWeatherIconKey(entries);

        return new WeatherResponse(
                roundToOneDecimal(tempMin),
                roundToOneDecimal(tempMax),
                roundToOneDecimal(avgFeelsLike),
                (int) Math.round(maxPop * 100),
                weatherIconKey
        );
    }

    // 강수확률이 가장 높은 시점의 condition을 대표값으로 삼는다 — 그 시점 날씨가 준비물 판단에 가장 중요하다.
    private String toWeatherIconKey(List<ForecastEntry> entries) {
        ForecastEntry worstEntry = entries.stream()
                .max(Comparator.comparingDouble(ForecastEntry::pop))
                .orElseThrow();
        int conditionId = worstEntry.weather().get(0).id();
        return TdsWeatherIcon.fromOwmCode(conditionId).assetKey();
    }

    private WeatherResponse toLastYearWeatherResponse(List<HistoricalDataPoint> points) {
        double tempMin = points.stream().mapToDouble(p -> p.temp().min()).min().orElseThrow();
        double tempMax = points.stream().mapToDouble(p -> p.temp().max()).max().orElseThrow();
        double avgFeelsLike = points.stream().mapToDouble(p -> p.feelsLike().day()).average().orElseThrow();
        double maxPop = points.stream().mapToDouble(HistoricalDataPoint::pop).max().orElse(0.0);
        String weatherIconKey = toLastYearWeatherIconKey(points);

        return new WeatherResponse(
                roundToOneDecimal(tempMin),
                roundToOneDecimal(tempMax),
                roundToOneDecimal(avgFeelsLike),
                (int) Math.round(maxPop * 100),
                weatherIconKey
        );
    }

    // 강수확률이 가장 높았던 날의 condition을 대표값으로 삼는다 — getWeather()의 기준과 동일.
    private String toLastYearWeatherIconKey(List<HistoricalDataPoint> points) {
        HistoricalDataPoint representative = points.stream()
                .max(Comparator.comparingDouble(HistoricalDataPoint::pop))
                .orElseThrow();
        int conditionId = representative.weather().get(0).id();
        return TdsWeatherIcon.fromOwmCode(conditionId).assetKey();
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

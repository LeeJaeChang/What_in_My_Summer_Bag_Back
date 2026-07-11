package com.example.demo.service;

import com.example.demo.client.CurrentWeatherResponse;
import com.example.demo.client.GeocodingResult;
import com.example.demo.client.OpenWeatherClient;
import com.example.demo.dto.WeatherResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final OpenWeatherClient openWeatherClient;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public WeatherService(OpenWeatherClient openWeatherClient) {
        this.openWeatherClient = openWeatherClient;
    }

    public WeatherResponse getWeather(String regionName) {
        CacheEntry cached = cache.get(regionName);
        if (cached != null && cached.isFresh()) {
            return cached.response();
        }

        String geocodingQuery = SupportedRegions.toGeocodingQuery(regionName)
                .orElseThrow(() -> new InvalidRegionException("지원하지 않는 지역명: " + regionName));

        GeocodingResult location = openWeatherClient.geocode(geocodingQuery)
                .orElseThrow(() -> new WeatherFetchFailedException("지역 좌표를 찾을 수 없습니다: " + regionName));

        CurrentWeatherResponse current = openWeatherClient.fetchCurrentWeather(location.lat(), location.lon());
        double precipitationProbability = openWeatherClient.fetchNearestPrecipitationProbability(location.lat(), location.lon());

        WeatherResponse response = toResponse(regionName, current, precipitationProbability);
        cache.put(regionName, new CacheEntry(response, Instant.now()));
        return response;
    }

    private WeatherResponse toResponse(String regionName, CurrentWeatherResponse current, double precipitationProbability) {
        String condition = current.weather().isEmpty() ? "정보 없음" : current.weather().get(0).description();
        OffsetDateTime updatedAt = Instant.ofEpochSecond(current.dt()).atOffset(ZoneOffset.ofHours(9));

        return new WeatherResponse(
                regionName,
                (int) Math.round(current.main().temp()),
                (int) Math.round(current.main().feelsLike()),
                (int) Math.round(precipitationProbability * 100),
                // TODO: OpenWeatherMap UV 전용 엔드포인트가 종료돼서 임시 고정값. One Call 3.0(카드 등록 필요) 붙이면 교체.
                "보통",
                condition,
                updatedAt
        );
    }

    private record CacheEntry(WeatherResponse response, Instant fetchedAt) {
        boolean isFresh() {
            return Instant.now().isBefore(fetchedAt.plus(CACHE_TTL));
        }
    }
}

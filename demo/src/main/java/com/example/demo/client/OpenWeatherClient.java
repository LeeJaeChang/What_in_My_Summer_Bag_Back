package com.example.demo.client;

import com.example.demo.service.WeatherFetchFailedException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OpenWeatherClient {

    private final RestClient restClient;
    private final String apiKey;

    public OpenWeatherClient(@Value("${openweather.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openweathermap.org")
                .build();
    }

    // query는 이미 로마자로 변환된 값("Gangneung,KR")을 받는다 — 지오코딩이 한글 지명을 인식하지 못함.
    // 결과 없음과 호출 실패를 구분해야 해서 Optional로 반환한다.
    public Optional<GeocodingResult> geocode(String query) {
        try {
            List<GeocodingResult> results = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/geo/1.0/direct")
                            .queryParam("q", query)
                            .queryParam("limit", 1)
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<GeocodingResult>>() {
                    });
            return results == null || results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (RestClientException e) {
            throw new WeatherFetchFailedException("지역 좌표 조회 실패: " + query, e);
        }
    }

    public CurrentWeatherResponse fetchCurrentWeather(double lat, double lon) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/data/2.5/weather")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .queryParam("lang", "kr")
                            .build())
                    .retrieve()
                    .body(CurrentWeatherResponse.class);
        } catch (RestClientException e) {
            throw new WeatherFetchFailedException("실시간 날씨 조회 실패", e);
        }
    }

    // 무료 티어에 강수확률이 있는 엔드포인트가 예보(forecast)뿐이라, 가장 가까운 3시간 슬롯의 pop을 사용한다.
    public double fetchNearestPrecipitationProbability(double lat, double lon) {
        try {
            ForecastResponse forecast = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/data/2.5/forecast")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .queryParam("lang", "kr")
                            .build())
                    .retrieve()
                    .body(ForecastResponse.class);
            if (forecast == null || forecast.list().isEmpty()) {
                return 0.0;
            }
            return forecast.list().get(0).pop();
        } catch (RestClientException e) {
            throw new WeatherFetchFailedException("강수확률 조회 실패", e);
        }
    }
}

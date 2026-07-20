package com.example.demo.recommend.client;

import com.example.demo.recommend.dto.RecommendRequest;
import com.example.demo.recommend.dto.WeatherSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class MockWeatherClient implements WeatherClient {

    @Override
    public WeatherSummaryResponse getWeather(
            RecommendRequest request
    ) {
        return new WeatherSummaryResponse(
                24.5,
                33.0,
                35.0,
                10
        );
    }
}
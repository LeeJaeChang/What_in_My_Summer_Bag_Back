package com.example.demo.recommend.client;

import com.example.demo.recommend.dto.RecommendRequest;
import com.example.demo.recommend.dto.WeatherSummaryResponse;

public interface WeatherClient {

    WeatherSummaryResponse getWeather(
            RecommendRequest request
    );
}
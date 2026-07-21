package com.example.demo.recommend.client;

import com.example.demo.dto.CreateTripRequest;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.recommend.dto.AiRecommendResult;

public interface AiRecommendClient {

    AiRecommendResult recommend(
            CreateTripRequest request,
            WeatherResponse weather
    );
}

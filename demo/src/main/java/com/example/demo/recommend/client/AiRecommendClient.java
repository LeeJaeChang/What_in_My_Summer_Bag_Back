package com.example.demo.recommend.client;

import com.example.demo.recommend.dto.AiRecommendResult;
import com.example.demo.recommend.dto.RecommendRequest;
import com.example.demo.recommend.dto.WeatherSummaryResponse;

public interface AiRecommendClient {

    AiRecommendResult recommend(
            RecommendRequest request,
            WeatherSummaryResponse weather
    );
}
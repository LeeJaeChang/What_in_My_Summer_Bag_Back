package com.example.demo.recommend.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RecommendResponse(

        @JsonProperty("recommendation_id")
        String recommendationId,

        @JsonProperty("weather_summary")
        WeatherSummaryResponse weatherSummary,

        List<RecommendItemResponse> items
) {
}

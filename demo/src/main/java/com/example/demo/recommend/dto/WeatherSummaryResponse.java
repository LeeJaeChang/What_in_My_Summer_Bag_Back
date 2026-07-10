package com.example.demo.recommend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WeatherSummaryResponse(

        int temperature,

        @JsonProperty("precipitation_probability")
        int precipitationProbability,

        String condition
) {
}

package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record WeatherResponse(
        @JsonProperty("region_name") String regionName,
        int temperature,
        @JsonProperty("feels_like") int feelsLike,
        @JsonProperty("precipitation_probability") int precipitationProbability,
        @JsonProperty("uv_index") String uvIndex,
        @JsonProperty("weather_condition") String weatherCondition,
        @JsonProperty("updated_at") OffsetDateTime updatedAt
) {
}

package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record WeatherResponse(
        @JsonProperty("region_name") String regionName,
        @JsonProperty("start_date") LocalDate startDate,
        @JsonProperty("end_date") LocalDate endDate,
        @JsonProperty("weather_summary") String weatherSummary,
        @JsonProperty("temperature_min") int temperatureMin,
        @JsonProperty("temperature_max") int temperatureMax,
        @JsonProperty("precipitation_probability") int precipitationProbability,
        @JsonProperty("updated_at") OffsetDateTime updatedAt
) {
}

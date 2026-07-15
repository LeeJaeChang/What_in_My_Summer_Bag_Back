package com.example.demo.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// OWM One Call 4.0 1-day 타임라인의 하루치 레코드.
public record HistoricalDataPoint(
        long dt,
        HistoricalTemperature temp,
        @JsonProperty("feels_like") HistoricalFeelsLike feelsLike,
        List<WeatherDescription> weather,
        double pop
) {
}

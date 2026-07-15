package com.example.demo.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record HistoricalDataPoint(
        long dt,
        double temp,
        @JsonProperty("feels_like") double feelsLike,
        List<WeatherDescription> weather,
        Precipitation rain,
        Precipitation snow
) {
    public double precipitationAmount() {
        double rainAmount = rain != null && rain.oneHour() != null ? rain.oneHour() : 0.0;
        double snowAmount = snow != null && snow.oneHour() != null ? snow.oneHour() : 0.0;
        return rainAmount + snowAmount;
    }
}

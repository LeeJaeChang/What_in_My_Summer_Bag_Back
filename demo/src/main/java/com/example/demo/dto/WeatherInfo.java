package com.example.demo.dto;

public record WeatherInfo(
        double temperatureMin,
        double temperatureMax,
        double temperaturePerceived,
        int precipitationProbability
) {
}

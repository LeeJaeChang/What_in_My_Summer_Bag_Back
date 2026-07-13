package com.example.demo.dto;

// Trip 상세 응답의 weather 객체
public record WeatherResponse(
        Double temperatureMin,
        Double temperatureMax,
        Double temperaturePerceived,
        Integer precipitationProbability
) {
}

package com.example.demo.recommend.dto;

public record WeatherSummaryResponse(

        Double temperatureMin,

        Double temperatureMax,

        Double temperaturePerceived,

        Integer precipitationProbability

) {
}
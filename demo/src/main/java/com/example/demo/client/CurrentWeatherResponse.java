package com.example.demo.client;

import java.util.List;

// OpenWeatherMap 실시간 날씨 API(/data/2.5/weather) 응답
public record CurrentWeatherResponse(List<WeatherDescription> weather, MainInfo main, long dt) {
}

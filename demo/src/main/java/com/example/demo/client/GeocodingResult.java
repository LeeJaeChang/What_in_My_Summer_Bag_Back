package com.example.demo.client;

// OpenWeatherMap Geocoding API(/geo/1.0/direct) 응답 원소 하나
public record GeocodingResult(String name, double lat, double lon, String country) {
}

package com.example.demo.controller;

import com.example.demo.dto.WeatherResponse;
import com.example.demo.service.WeatherService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // 담당: 본인 (영건) — API 명세서 1번
    @GetMapping("/weather")
    public ResponseEntity<Map<String, Object>> getWeather(@RequestParam("region_name") String regionName) {
        WeatherResponse result = weatherService.getWeather(regionName);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }
}

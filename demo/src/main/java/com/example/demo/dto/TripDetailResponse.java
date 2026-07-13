package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

// POST /trips 201 및 GET /trips/{tripId} 200 공통 응답
public record TripDetailResponse(
        Long tripId,
        String destination,
        LocalDate startDate,
        LocalDate endDate,
        WeatherResponse weather,
        List<String> activities,
        String travelTip,
        List<PackingItemResponse> packingItems
) {
}

package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

// GET /trips 목록의 개별 항목
public record TripSummaryResponse(
        Long tripId,
        String destination,
        LocalDate startDate,
        LocalDate endDate,
        String weatherIconKey,
        LocalDateTime createdAt
) {
}

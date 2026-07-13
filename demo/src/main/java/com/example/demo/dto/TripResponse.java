package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public record TripResponse(
        Long memberId,
        Long tripId,
        String destination,
        LocalDate startDate,
        LocalDate endDate,
        List<String> activities,
        List<PackingItemResponse> items,
        int readinessPercent
) {
}

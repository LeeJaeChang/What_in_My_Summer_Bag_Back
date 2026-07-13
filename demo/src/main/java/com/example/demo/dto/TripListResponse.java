package com.example.demo.dto;

import java.util.List;

// GET /trips
public record TripListResponse(
        List<TripSummaryResponse> trips,
        long totalCount
) {
}

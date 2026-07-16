package com.example.demo.recommend.dto;

import com.example.demo.entity.ActivityType;

import java.time.LocalDate;
import java.util.List;

public record RecommendResponse(

        Long tripId,

        String destination,

        LocalDate startDate,

        LocalDate endDate,

        WeatherSummaryResponse weather,

        List<ActivityType> activities,

        String travelTip,

        List<RecommendItemResponse> packingItems

) {
}
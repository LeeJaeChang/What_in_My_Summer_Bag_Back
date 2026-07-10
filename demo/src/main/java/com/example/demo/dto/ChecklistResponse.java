package com.example.demo.dto;

import java.util.List;

public record ChecklistResponse(
        String userId,
        String checklistId,
        String regionName,
        List<ChecklistItemResponse> items,
        int readinessPercent
) {
}

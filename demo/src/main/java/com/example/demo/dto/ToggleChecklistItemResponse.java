package com.example.demo.dto;

public record ToggleChecklistItemResponse(
        String checklistItemId,
        boolean checked,
        int readinessPercent
) {
}

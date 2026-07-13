package com.example.demo.dto;

public record TogglePackingItemResponse(
        Long packingItemId,
        boolean checked,
        int readinessPercent
) {
}

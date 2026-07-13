package com.example.demo.dto;

public record PackingItemResponse(
        Long packingItemId,
        String name,
        String category,
        boolean checked
) {
}

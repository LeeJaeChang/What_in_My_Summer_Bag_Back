package com.example.demo.dto;

public record PackingItemResponse(
        Long id,
        String name,
        String category,
        String reason,
        String iconKey,
        boolean checked,
        Integer sortOrder
) {
}

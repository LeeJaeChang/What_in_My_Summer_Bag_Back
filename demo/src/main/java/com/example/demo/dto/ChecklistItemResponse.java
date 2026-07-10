package com.example.demo.dto;

public record ChecklistItemResponse(
        String checklistItemId,
        String name,
        boolean checked
) {
}

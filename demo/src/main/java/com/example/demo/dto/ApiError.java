package com.example.demo.dto;

public record ApiError(
        String code,
        String message
) {
}

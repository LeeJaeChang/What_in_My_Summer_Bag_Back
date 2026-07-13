package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

// POST /auth/login
public record LoginRequest(
        @NotBlank String tossToken
) {
}

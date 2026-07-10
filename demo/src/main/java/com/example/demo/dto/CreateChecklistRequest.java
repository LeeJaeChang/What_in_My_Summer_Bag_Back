package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateChecklistRequest(
        @NotBlank String recommendationId
) {
}

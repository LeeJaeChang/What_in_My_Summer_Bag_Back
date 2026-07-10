package com.example.demo.dto;

public record ApiErrorResponse(
        boolean success,
        ApiError error
) {
    // API 명세서의 공통 에러 포맷은 항상 success=false. 편의 생성자로 고정.
    public ApiErrorResponse(ApiError error) {
        this(false, error);
    }
}

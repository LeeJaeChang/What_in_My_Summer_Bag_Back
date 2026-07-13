package com.example.demo.dto;

/**
 * 공통 에러 응답 포맷 (API 명세서 v2): { "errorCode": "...", "message": "..." }
 */
public record ErrorResponse(
        String errorCode,
        String message
) {
}

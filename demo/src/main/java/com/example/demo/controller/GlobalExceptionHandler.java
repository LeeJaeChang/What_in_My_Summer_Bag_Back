package com.example.demo.controller;

import com.example.demo.auth.UnauthorizedException;
import com.example.demo.dto.ErrorResponse;
import com.example.demo.service.ForbiddenException;
import com.example.demo.service.InvalidDateRangeException;
import com.example.demo.service.InvalidQueryException;
import com.example.demo.service.InvalidRegionException;
import com.example.demo.service.MemberNotFoundException;
import com.example.demo.service.PackingItemNotFoundException;
import com.example.demo.service.TripNotFoundException;
import com.example.demo.service.UnsupportedDateRangeException;
import com.example.demo.service.WeatherFetchFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// API 명세서 v2의 공통 에러 응답 포맷({ errorCode, message })을 그대로 맞춘다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MemberNotFoundException.class, TripNotFoundException.class,
            PackingItemNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException e) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage(), "리소스를 찾을 수 없습니다.");
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e) {
        return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", e.getMessage(), "인증에 실패했습니다.");
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException e) {
        return build(HttpStatus.FORBIDDEN, "FORBIDDEN", e.getMessage(), "권한이 없습니다.");
    }

    @ExceptionHandler(InvalidRegionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRegion(InvalidRegionException e) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_REGION", e.getMessage(), "지원하지 않는 지역입니다.");
    }

    @ExceptionHandler(InvalidQueryException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQuery(InvalidQueryException e) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_QUERY", e.getMessage(), "검색어 형식이 올바르지 않습니다.");
    }

    @ExceptionHandler({InvalidDateRangeException.class, UnsupportedDateRangeException.class})
    public ResponseEntity<ErrorResponse> handleInvalidDateRange(RuntimeException e) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_DATE_RANGE", e.getMessage(), "요청한 날짜 범위가 올바르지 않습니다.");
    }

    @ExceptionHandler(WeatherFetchFailedException.class)
    public ResponseEntity<ErrorResponse> handleWeatherFetchFailed(WeatherFetchFailedException e) {
        return build(HttpStatus.BAD_GATEWAY, "WEATHER_FETCH_FAILED", e.getMessage(), "날씨 정보를 가져오지 못했습니다.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", e.getMessage(), "잘못된 요청입니다.");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message, String fallback) {
        String body = message != null ? message : fallback;
        return ResponseEntity.status(status).body(new ErrorResponse(code, body));
    }
}

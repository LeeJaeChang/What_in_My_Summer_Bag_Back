package com.example.demo.controller;

import com.example.demo.dto.ApiError;
import com.example.demo.dto.ApiErrorResponse;
import com.example.demo.service.PackingItemNotFoundException;
import com.example.demo.service.TripNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// API 명세서의 공통 에러 응답 포맷({ success:false, error:{code,message} })을 그대로 맞춘다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTripNotFound(TripNotFoundException e) {
        String message = e.getMessage() != null ? e.getMessage() : "여행 계획을 찾을 수 없습니다.";
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(new ApiError("TRIP_NOT_FOUND", message)));
    }

    @ExceptionHandler(PackingItemNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePackingItemNotFound(PackingItemNotFoundException e) {
        String message = e.getMessage() != null ? e.getMessage() : "항목을 찾을 수 없습니다.";
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(new ApiError("PACKING_ITEM_NOT_FOUND", message)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(IllegalArgumentException e) {
        String message = e.getMessage() != null ? e.getMessage() : "권한이 없습니다.";
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse(new ApiError("FORBIDDEN", message)));
    }
}

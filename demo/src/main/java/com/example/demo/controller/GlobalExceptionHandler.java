package com.example.demo.controller;

import com.example.demo.dto.ApiError;
import com.example.demo.dto.ApiErrorResponse;
import com.example.demo.service.ChecklistNotFoundException;
import com.example.demo.service.InvalidRegionException;
import com.example.demo.service.ItemNotFoundException;
import com.example.demo.service.WeatherFetchFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// API 명세서의 공통 에러 응답 포맷({ success:false, error:{code,message} })을 그대로 맞춘다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChecklistNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleChecklistNotFound(ChecklistNotFoundException e) {
        String message = e.getMessage() != null ? e.getMessage() : "체크리스트를 찾을 수 없습니다.";
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(new ApiError("CHECKLIST_NOT_FOUND", message)));
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleItemNotFound(ItemNotFoundException e) {
        String message = e.getMessage() != null ? e.getMessage() : "항목을 찾을 수 없습니다.";
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(new ApiError("ITEM_NOT_FOUND", message)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(IllegalArgumentException e) {
        String message = e.getMessage() != null ? e.getMessage() : "권한이 없습니다.";
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse(new ApiError("FORBIDDEN", message)));
    }

    @ExceptionHandler(InvalidRegionException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRegion(InvalidRegionException e) {
        String message = e.getMessage() != null ? e.getMessage() : "잘못된 지역명입니다.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(new ApiError("INVALID_REGION", message)));
    }

    @ExceptionHandler(WeatherFetchFailedException.class)
    public ResponseEntity<ApiErrorResponse> handleWeatherFetchFailed(WeatherFetchFailedException e) {
        String message = e.getMessage() != null ? e.getMessage() : "날씨 정보를 가져오지 못했습니다.";
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiErrorResponse(new ApiError("WEATHER_FETCH_FAILED", message)));
    }
}

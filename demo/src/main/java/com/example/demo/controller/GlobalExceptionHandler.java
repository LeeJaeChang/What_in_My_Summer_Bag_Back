package com.example.demo.controller;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.service.ForbiddenException;
import com.example.demo.service.MemberNotFoundException;
import com.example.demo.service.PackingItemNotFoundException;
import com.example.demo.service.TripNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// API 명세서 v2 공통 에러 포맷({ errorCode, message })을 그대로 맞춘다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTripNotFound(TripNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "TRIP_NOT_FOUND", e.getMessage(), "해당 여행 계획을 찾을 수 없습니다.");
    }

    @ExceptionHandler(PackingItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePackingItemNotFound(PackingItemNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "PACKING_ITEM_NOT_FOUND", e.getMessage(), "해당 준비물을 찾을 수 없습니다.");
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFound(MemberNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", e.getMessage(), "회원을 찾을 수 없습니다.");
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException e) {
        return build(HttpStatus.FORBIDDEN, "FORBIDDEN", e.getMessage(), "접근 권한이 없습니다.");
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", e.getMessage(), "요청 값이 올바르지 않습니다.");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message, String fallback) {
        String resolved = (message != null && !message.isBlank()) ? message : fallback;
        return ResponseEntity.status(status).body(new ErrorResponse(code, resolved));
    }
}

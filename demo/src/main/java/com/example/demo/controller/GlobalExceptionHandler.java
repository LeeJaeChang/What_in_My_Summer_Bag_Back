package com.example.demo.controller;

import com.example.demo.auth.TossAuthException;
import com.example.demo.auth.UnauthorizedException;
import com.example.demo.dto.ErrorResponse;
import com.example.demo.service.AiRecommendFailedException;
import com.example.demo.service.ForbiddenException;
import com.example.demo.service.InvalidDateRangeException;
import com.example.demo.service.InvalidQueryException;
import com.example.demo.service.InvalidRegionException;
import com.example.demo.service.MemberNotFoundException;
import com.example.demo.service.PackingItemNotFoundException;
import com.example.demo.service.PurchaseLinkNotFoundException;
import com.example.demo.service.TripNotFoundException;
import com.example.demo.service.UnsupportedDateRangeException;
import com.example.demo.service.WeatherFetchFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// API 명세서 v2의 공통 에러 응답 포맷({ errorCode, message })을 그대로 맞춘다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 는 프론트가 코드로 분기하므로 리소스별 errorCode 를 그대로 내보낸다(명세 기준).
    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTripNotFound(TripNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "TRIP_NOT_FOUND", e.getMessage(), "해당 여행 계획을 찾을 수 없습니다.");
    }

    @ExceptionHandler(PackingItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePackingItemNotFound(PackingItemNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "PACKING_ITEM_NOT_FOUND", e.getMessage(), "해당 준비물을 찾을 수 없습니다.");
    }

    @ExceptionHandler(PurchaseLinkNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePurchaseLinkNotFound(PurchaseLinkNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "PURCHASE_LINK_NOT_FOUND", e.getMessage(),
                "해당 준비물의 구매 링크를 찾을 수 없습니다.");
    }

    // 명세에는 없지만 토큰의 member_id 가 DB에 없는 경우를 구분하기 위해 별도 코드로 둔다.
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFound(MemberNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", e.getMessage(), "해당 회원을 찾을 수 없습니다.");
    }

    // 토스 연동 실패는 클라이언트가 상황을 구분할 수 있도록 명세의 errorCode 를 그대로 내보낸다.
    @ExceptionHandler(TossAuthException.class)
    public ResponseEntity<ErrorResponse> handleTossAuth(TossAuthException e) {
        return build(e.getStatus(), e.getErrorCode(), e.getMessage(), "토스 로그인에 실패했습니다.");
    }

    // @Valid 실패(authorizationCode/referrer 누락) → MISSING_PARAMETER
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MethodArgumentNotValidException e) {
        String field = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getField)
                .orElse(null);
        String message = field == null ? null : "필수 파라미터가 없습니다: " + field;
        return build(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", message, "필수 파라미터가 없습니다.");
    }

    // Authorization 헤더 자체가 없으면 스프링이 MissingRequestHeaderException 을 던진다.
    // 기본 처리(400 + 스프링 기본 포맷) 대신 명세의 401 + 공통 에러 포맷으로 내보낸다.
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException e) {
        if (!HttpHeaders.AUTHORIZATION.equalsIgnoreCase(e.getHeaderName())) {
            return build(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER",
                    "필수 헤더가 없습니다: " + e.getHeaderName(), "필수 헤더가 없습니다.");
        }
        return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED",
                "Authorization 헤더가 없습니다.", "인증에 실패했습니다.");
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e) {
        return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", e.getMessage(), "인증에 실패했습니다.");
    }

    // 현재 403 은 trip 소유권 검증에서만 발생하므로 명세의 FORBIDDEN_TRIP_ACCESS 로 내보낸다.
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException e) {
        return build(HttpStatus.FORBIDDEN, "FORBIDDEN_TRIP_ACCESS", e.getMessage(), "다른 회원의 여행 계획입니다.");
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
        // 명세는 날씨 조회 실패를 500(서버 내부 오류)으로 규정한다.
        // 500 은 응답 본문에 원인을 담지 않으므로 로그에는 스택트레이스까지 남긴다.
        log.error("날씨 조회 실패", e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "WEATHER_FETCH_FAILED", e.getMessage(),
                "날씨 정보를 가져오지 못했습니다.");
    }

    @ExceptionHandler(AiRecommendFailedException.class)
    public ResponseEntity<ErrorResponse> handleAiRecommendFailed(AiRecommendFailedException e) {
        // 명세는 AI 생성 실패를 500(서버 내부 오류)으로 규정한다.
        // 원인(429 할당량 초과 / 503 과부하 / 파싱 실패)은 응답에 안 나가므로 반드시 로그로 남긴다.
        log.error("AI 추천 생성 실패", e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "AI_RECOMMEND_FAILED", e.getMessage(),
                "준비물 추천을 생성하지 못했습니다.");
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

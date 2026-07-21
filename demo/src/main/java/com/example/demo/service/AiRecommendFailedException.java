package com.example.demo.service;

/**
 * AI 추천 생성 실패. 명세상 500(서버 내부 오류 — 날씨 조회/AI 생성 실패 포함)으로 내려간다.
 * Gemini 호출 실패(429 할당량 초과, 503 과부하 등)와 응답 파싱 실패를 모두 이 예외로 모은다.
 */
public class AiRecommendFailedException extends RuntimeException {

    public AiRecommendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

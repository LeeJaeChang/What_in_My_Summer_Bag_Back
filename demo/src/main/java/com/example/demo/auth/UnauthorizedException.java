package com.example.demo.auth;

// 인증 실패(토큰 누락/위조/만료, 토스 검증 실패) — HTTP 401.
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}

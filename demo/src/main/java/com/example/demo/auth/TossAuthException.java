package com.example.demo.auth;

import org.springframework.http.HttpStatus;

/**
 * 토스 연동 중 발생한 실패를 API 명세의 (HTTP status, errorCode) 쌍으로 옮긴 예외.
 * 매핑은 {@link com.example.demo.client.TossApiClient} 가 토스 응답을 보고 결정한다.
 */
public class TossAuthException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public TossAuthException(HttpStatus status, String errorCode, String message) {
        this(status, errorCode, message, null);
    }

    public TossAuthException(HttpStatus status, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

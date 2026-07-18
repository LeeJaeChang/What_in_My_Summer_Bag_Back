package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * POST /auth/login — 클라이언트는 토큰을 들고 있지 않고, appLogin()이 반환한 일회성 인가 코드만 넘긴다.
 * 토큰 교환·사용자 조회는 전부 서버에서 처리한다.
 */
public record LoginRequest(
        // appLogin()이 반환한 인가 코드. 유효 10분, 일회성(재사용 시 실패).
        @NotBlank String authorizationCode,
        // 로그인 발생 환경. DEFAULT(실제 토스 앱) / SANDBOX. 그대로 토스 토큰 교환에 전달한다.
        @NotBlank String referrer
) {
}

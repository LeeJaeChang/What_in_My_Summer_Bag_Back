package com.example.demo.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * POST /api-partner/v1/apps-in-toss/user/oauth2/generate-token 응답.
 * refreshToken/expiresIn 등은 로그인 이후 토스 API를 호출할 일이 없어 저장하지 않고 버린다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TossTokenResponse(
        // 유효 1시간. 바로 뒤의 login-me 호출에만 쓰고 버린다.
        String accessToken,
        // 유효 14일. 현재 설계상 갱신할 일이 없어 사용하지 않는다.
        String refreshToken,
        Long expiresIn,
        String tokenType
) {
}

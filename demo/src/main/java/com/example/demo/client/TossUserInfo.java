package com.example.demo.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * GET /api-partner/v1/apps-in-toss/user/oauth2/login-me 응답.
 *
 * userKey 는 앱 단위 고유값이라 우리 서비스 내부 매핑 키로 쓴다.
 * scope 는 토스가 항목을 추가할 수 있어(2026-01-02 user_key 추가) 문자열 리스트로만 받고 해석하지 않는다.
 * 정의되지 않은 값이 들어와도 역직렬화가 깨지지 않게 enum 으로 만들지 않는다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TossUserInfo(
        Long userKey,
        List<String> scope
) {
}

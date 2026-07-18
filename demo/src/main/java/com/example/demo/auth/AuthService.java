package com.example.demo.auth;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;

public interface AuthService {

    // POST /auth/login — 인가 코드로 토스 사용자를 식별해 Member를 upsert 하고 자체 accessToken을 발급한다.
    LoginResponse login(LoginRequest request);

    // Authorization 헤더(accessToken)를 검증해서 내부 member_id를 반환한다.
    Long resolveMemberId(String authorizationHeader);
}

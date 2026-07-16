package com.example.demo.auth;

import com.example.demo.dto.LoginResponse;

public interface AuthService {

    // POST /auth/login — 토스 토큰으로 로그인(최초면 Member 생성) 후 accessToken 발급
    LoginResponse login(String tossToken);

    // Authorization 헤더(accessToken)를 검증해서 내부 member_id를 반환한다.
    Long resolveMemberId(String authorizationHeader);
}

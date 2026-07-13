package com.example.demo.auth;

public interface AuthService {

    // Authorization 헤더를 검증해서 내부 member_id를 반환한다.
    Long resolveMemberId(String authorizationHeader);
}

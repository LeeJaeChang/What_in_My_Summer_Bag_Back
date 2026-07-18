package com.example.demo.dto;

/**
 * 유저 정보를 노출하는 화면이 없어 nickname 은 응답에 포함하지 않는다.
 */
public record LoginResponse(
        // 우리 DB의 Member PK (토스 userKey 아님)
        Long memberId,
        // 우리 서비스가 발급한 JWT. 이후 모든 API의 Authorization: Bearer 값. 토스 accessToken 과 별개.
        String accessToken,
        boolean isNewMember
) {
}

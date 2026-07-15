package com.example.demo.dto;

import java.time.LocalDateTime;

// GET /members/me — 명세에 상세 필드가 없어 기본 프로필 필드로 구성
public record MemberResponse(
        Long memberId,
        String nickname,
        LocalDateTime createdAt
) {
}

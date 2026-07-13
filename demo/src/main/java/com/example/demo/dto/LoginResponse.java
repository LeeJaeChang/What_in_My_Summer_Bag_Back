package com.example.demo.dto;

public record LoginResponse(
        Long memberId,
        String nickname,
        String accessToken,
        boolean isNewMember
) {
}

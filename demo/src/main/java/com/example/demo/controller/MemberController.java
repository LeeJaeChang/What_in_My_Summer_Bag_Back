package com.example.demo.controller;

import com.example.demo.auth.AuthService;
import com.example.demo.dto.MemberResponse;
import com.example.demo.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/members")
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

    public MemberController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMe(@RequestHeader("Authorization") String authorization) {
        Long memberId = authService.resolveMemberId(authorization);
        return ResponseEntity.ok(memberService.getMe(memberId));
    }
}

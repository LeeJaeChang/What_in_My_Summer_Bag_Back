package com.example.demo.auth;

import com.example.demo.client.TossApiClient;
import com.example.demo.client.TossUserInfo;
import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 실제 인증 구현. 토스 서버 검증 + 자체 JWT 발급.
 *
 * 활성화: application.properties 의 auth.mode=toss (기본값은 stub → {@link StubAuthService}).
 * 스텁은 그대로 두고 이 프로퍼티만 바꾸면 활성 빈이 교체된다.
 */
@Service
@ConditionalOnProperty(name = "auth.mode", havingValue = "toss")
public class TossAuthService implements AuthService {

    private final MemberRepository memberRepository;
    private final TossApiClient tossApiClient;
    private final JwtProvider jwtProvider;

    public TossAuthService(
            MemberRepository memberRepository,
            TossApiClient tossApiClient,
            JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.tossApiClient = tossApiClient;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public LoginResponse login(String tossToken) {
        // 1. 토스 서버에 토큰 검증 → userKey/nickname 획득
        TossUserInfo info = tossApiClient.verify(tossToken);

        // 2. members upsert (최초 로그인 시 생성)
        Member existing = memberRepository.findByTossUserKey(info.userKey()).orElse(null);
        boolean isNewMember = existing == null;
        Member member;
        if (isNewMember) {
            member = memberRepository.save(new Member(info.userKey(), info.nickname()));
        } else {
            member = existing;
            // 토스 쪽 닉네임이 바뀌었으면 반영한다.
            if (info.nickname() != null && !info.nickname().equals(member.getNickname())) {
                member.setNickname(info.nickname());
                member = memberRepository.save(member);
            }
        }

        // 3. 자체 JWT 발급 → 이후 요청은 이 accessToken 을 Bearer 로 보낸다
        String accessToken = jwtProvider.issue(member.getId());
        return new LoginResponse(member.getId(), member.getNickname(), accessToken, isNewMember);
    }

    @Override
    public Long resolveMemberId(String authorizationHeader) {
        String token = extractBearer(authorizationHeader);
        return jwtProvider.parseMemberId(token);
    }

    private String extractBearer(String header) {
        if (header == null || header.isBlank()) {
            throw new UnauthorizedException("Authorization 헤더가 없습니다.");
        }
        String token = header.replaceFirst("^Bearer ", "").trim();
        if (token.isBlank()) {
            throw new UnauthorizedException("Authorization 헤더가 비어 있습니다.");
        }
        return token;
    }
}

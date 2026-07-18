package com.example.demo.auth;

import com.example.demo.client.TossApiClient;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 실제 인증 구현. 토스 인가 코드 → tossAccessToken → userKey → Member upsert → 자체 JWT 발급.
 *
 * 활성화: application.properties 의 auth.mode=toss (기본값은 stub → {@link StubAuthService}).
 * 스텁은 그대로 두고 이 프로퍼티만 바꾸면 활성 빈이 교체된다.
 *
 * 토스 accessToken/refreshToken 은 저장하지 않는다. 로그인 이후 토스 API를 호출할 일이 없어
 * refreshToken 14일 만료를 관리할 이유가 없다(호출이 생기면 저장·갱신 정책부터 정할 것).
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
    public LoginResponse login(LoginRequest request) {
        // ① 인가 코드 → tossAccessToken (유효 1시간, 여기서만 쓰고 버린다)
        String tossAccessToken = tossApiClient.generateToken(request.authorizationCode(), request.referrer());

        // ② tossAccessToken → userKey (앱 단위 고유값이라 우리 내부 매핑 키로 쓴다)
        Long userKey = tossApiClient.fetchUserKey(tossAccessToken);

        // ③ Member upsert (최초 로그인 시 생성)
        Member member = memberRepository.findByTossUserKey(userKey).orElse(null);
        boolean isNewMember = member == null;
        if (isNewMember) {
            member = memberRepository.save(new Member(userKey));
        }

        // ④ 자체 JWT 발급 → 이후 요청은 이 accessToken 을 Bearer 로 보낸다(토스 토큰과 별개)
        String accessToken = jwtProvider.issue(member.getId());
        return new LoginResponse(member.getId(), accessToken, isNewMember);
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

package com.example.demo.auth;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 개발/테스트용 임시 인증 구현(기본 활성). 실제 구현은 {@link TossAuthService}.
 *
 * 활성화: application.properties 의 auth.mode=stub (미설정 시 기본값). auth.mode=toss 로 바꾸면
 * 실제 구현(TossAuthService)으로 교체된다. 두 구현이 동시에 빈으로 뜨지 않도록 조건부로 등록한다.
 *
 * 지금 스텁은 authorizationCode == tossUserKey(숫자)로 간주하고, accessToken도 그 값을 그대로 돌려준다.
 * 따라서 이후 요청은 "Authorization: Bearer {tossUserKey}"로 보내면 된다. 로컬 개발/연동 테스트용.
 * referrer 는 스텁에선 사용하지 않는다(토스 호출이 없으므로).
 */
@Service
@ConditionalOnProperty(name = "auth.mode", havingValue = "stub", matchIfMissing = true)
public class StubAuthService implements AuthService {

    private final MemberRepository memberRepository;

    public StubAuthService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Long tossUserKey = parseTossUserKey(request.authorizationCode());

        // TODO: 토스 서버 검증으로 실제 userKey를 받아온다. 지금은 스텁.
        Member member = memberRepository.findByTossUserKey(tossUserKey).orElse(null);
        boolean isNewMember = member == null;
        if (isNewMember) {
            member = memberRepository.save(new Member(tossUserKey));
        }

        // TODO: 자체 JWT 발급으로 교체. 지금은 authorizationCode를 그대로 accessToken처럼 사용.
        return new LoginResponse(member.getId(), request.authorizationCode(), isNewMember);
    }

    @Override
    public Long resolveMemberId(String authorizationHeader) {
        String raw = authorizationHeader == null
                ? ""
                : authorizationHeader.replaceFirst("^Bearer ", "").trim();
        // 토큰 관련 실패는 401 이어야 한다(명세 기준). TossAuthService 와 동작을 맞춘다 —
        // 스텁만 400 을 내면 프론트가 스텁으로 개발할 때 401 분기를 검증할 수 없다.
        if (raw.isBlank()) {
            throw new UnauthorizedException("Authorization 헤더가 없습니다.");
        }

        Long tossUserKey;
        try {
            tossUserKey = parseTossUserKey(raw);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다: " + raw, e);
        }
        return memberRepository.findByTossUserKey(tossUserKey)
                .map(Member::getId)
                .orElseGet(() -> memberRepository.save(new Member(tossUserKey)).getId());
    }

    private Long parseTossUserKey(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("authorizationCode(스텁)는 숫자여야 합니다: " + value);
        }
    }
}

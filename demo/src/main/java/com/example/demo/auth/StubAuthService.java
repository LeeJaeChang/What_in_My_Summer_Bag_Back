package com.example.demo.auth;

import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import org.springframework.stereotype.Service;

/**
 * TODO(재창): 실제 앱인토스 SDK 연동 + JWT 발급으로 교체 필요. 지금은 개발/테스트용 임시 구현이다.
 *
 * 실제 흐름:
 * 1. login(tossToken): 토스 서버에 토큰 검증/사용자 조회 -> userKey 획득 -> members upsert -> 자체 JWT 발급
 * 2. resolveMemberId(header): 자체 발급 JWT를 검증해서 member_id 판별
 *    (클라이언트가 member_id를 직접 보내지 않게 하는 것이 핵심 보안 요구사항)
 *
 * 지금 스텁은 tossToken == tossUserKey(숫자)로 간주하고, accessToken도 그 값을 그대로 돌려준다.
 * 따라서 이후 요청은 "Authorization: Bearer {tossUserKey}"로 보내면 된다. 로컬 개발/연동 테스트용.
 */
@Service
public class StubAuthService implements AuthService {

    private final MemberRepository memberRepository;

    public StubAuthService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public LoginResponse login(String tossToken) {
        Long tossUserKey = parseTossUserKey(tossToken);

        // TODO: 토스 서버 검증으로 실제 nickname/userKey를 받아온다. 지금은 스텁.
        Member member = memberRepository.findByTossUserKey(tossUserKey).orElse(null);
        boolean isNewMember = member == null;
        if (isNewMember) {
            member = memberRepository.save(new Member(tossUserKey, null));
        }

        // TODO: 자체 JWT 발급으로 교체. 지금은 tossToken을 그대로 accessToken처럼 사용.
        String accessToken = tossToken;
        return new LoginResponse(member.getId(), member.getNickname(), accessToken, isNewMember);
    }

    @Override
    public Long resolveMemberId(String authorizationHeader) {
        String raw = authorizationHeader == null
                ? ""
                : authorizationHeader.replaceFirst("^Bearer ", "").trim();
        if (raw.isBlank()) {
            throw new IllegalArgumentException("Authorization 헤더가 없습니다.");
        }

        Long tossUserKey = parseTossUserKey(raw);
        return memberRepository.findByTossUserKey(tossUserKey)
                .map(Member::getId)
                .orElseGet(() -> memberRepository.save(new Member(tossUserKey, null)).getId());
    }

    private Long parseTossUserKey(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("tossToken(스텁)은 숫자여야 합니다: " + value);
        }
    }
}

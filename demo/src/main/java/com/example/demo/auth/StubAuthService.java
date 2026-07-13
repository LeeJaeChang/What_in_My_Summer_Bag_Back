package com.example.demo.auth;

import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import org.springframework.stereotype.Service;

/**
 * TODO(재창): 실제 앱인토스 SDK 연동으로 교체 필요. 지금은 개발/테스트용 임시 구현이다.
 *
 * 실제로 구현해야 할 흐름 (API 명세서 초안.md 기준):
 * 1. 프론트가 appLogin()으로 받은 "인가 코드"를 백엔드로 전달
 * 2. 백엔드가 그 인가 코드로 토스 서버에 access_token 교환 요청 (toss.client-id/secret 사용)
 * 3. access_token으로 토스 사용자 정보 조회 API 호출 -> userKey 획득
 * 4. userKey로 members 테이블 upsert, 이후 자체 세션/JWT를 발급해서 클라이언트에 내려줌
 * 5. 이후 모든 요청은 그 자체 발급 토큰을 검증해서 member_id를 판별
 *    (클라이언트가 member_id를 직접 보내지 않게 하는 것이 핵심 보안 요구사항)
 *
 * 지금 구현은 "Authorization: Bearer {tossUserKey}"를 그대로 tossUserKey로 취급해서
 * upsert만 해주는 최소 버전 — 로컬 개발/다른 파트 연동 테스트용.
 */
@Service
public class StubAuthService implements AuthService {

    private final MemberRepository memberRepository;

    public StubAuthService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Long resolveMemberId(String authorizationHeader) {
        String raw = authorizationHeader == null
                ? ""
                : authorizationHeader.replaceFirst("^Bearer ", "").trim();
        if (raw.isBlank()) {
            throw new IllegalArgumentException("Authorization 헤더가 없습니다.");
        }

        Long tossUserKey;
        try {
            tossUserKey = Long.parseLong(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("tossUserKey는 숫자여야 합니다: " + raw);
        }

        return memberRepository.findByTossUserKey(tossUserKey)
                .map(Member::getId)
                .orElseGet(() -> {
                    Member newMember = new Member(tossUserKey, null);
                    return memberRepository.save(newMember).getId();
                });
    }
}

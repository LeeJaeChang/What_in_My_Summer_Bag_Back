package com.example.demo.client;

import com.example.demo.auth.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * 앱인토스 서버에 accessToken(tossToken)을 검증하고 사용자 정보를 받아온다.
 *
 * TODO(재창): 실제 앱인토스 API 명세에 맞춰 아래를 확정할 것.
 *   - 엔드포인트 경로(verifyPath)와 HTTP 메서드
 *   - 토큰 전달 방식(지금은 Authorization: Bearer 헤더로 가정)
 *   - 응답 JSON 필드명(userKey / nickname) — {@link TossUserInfo} 참고
 * 구조는 완성되어 있으므로 명세 확정 후 매핑만 맞추면 동작한다.
 */
@Component
public class TossApiClient {

    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;
    private final String verifyPath;

    public TossApiClient(
            @Value("${toss.base-url}") String baseUrl,
            @Value("${toss.verify-path}") String verifyPath,
            @Value("${toss.client-id}") String clientId,
            @Value("${toss.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.verifyPath = verifyPath;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    // tossToken을 토스 서버에서 검증하고 사용자 정보를 반환한다. 검증 실패 시 UnauthorizedException.
    public TossUserInfo verify(String tossToken) {
        try {
            TossUserInfo info = restClient.get()
                    .uri(verifyPath)
                    .header("Authorization", "Bearer " + tossToken)
                    // 앱마다 client 자격증명 전달 방식이 다를 수 있어 헤더로도 함께 실어 보낸다(명세 확정 시 조정).
                    .header("X-Toss-Client-Id", clientId)
                    .header("X-Toss-Client-Secret", clientSecret)
                    .retrieve()
                    .body(TossUserInfo.class);
            if (info == null || info.userKey() == null) {
                throw new UnauthorizedException("토스 사용자 정보를 확인할 수 없습니다.");
            }
            return info;
        } catch (RestClientException e) {
            // 4xx(토큰 무효)든 5xx(토스 장애)든 클라이언트 입장에선 인증 실패로 처리한다.
            throw new UnauthorizedException("토스 토큰 검증에 실패했습니다.", e);
        }
    }
}

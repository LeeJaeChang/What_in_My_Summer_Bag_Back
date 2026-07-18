package com.example.demo.client;

import com.example.demo.auth.TossAuthException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * 앱인토스 서버와의 통신. 인가 코드 → tossAccessToken 교환(①), tossAccessToken → userKey 조회(②).
 *
 * 두 호출 모두 mTLS 구간이라 {@link com.example.demo.config.TossClientConfig} 가 만든 RestClient 를 쓴다.
 * 토스가 내려주는 실패 사유를 API 명세의 errorCode 로 옮기는 것도 이 클래스 책임이다.
 */
@Component
public class TossApiClient {

    // 토스 에러 응답의 봉투 모양이 엔드포인트마다 다를 수 있어, 코드 필드만 관대하게 긁어낸다.
    private static final Pattern ERROR_CODE = Pattern.compile(
            "\"(?:errorCode|error_code|error|code)\"\\s*:\\s*\"([^\"]+)\"");

    private final RestClient restClient;
    private final String generateTokenPath;
    private final String loginMePath;

    public TossApiClient(
            @Qualifier("tossRestClient") RestClient restClient,
            @Value("${toss.generate-token-path}") String generateTokenPath,
            @Value("${toss.login-me-path}") String loginMePath) {
        this.restClient = restClient;
        this.generateTokenPath = generateTokenPath;
        this.loginMePath = loginMePath;
    }

    /** ① 일회성 인가 코드를 tossAccessToken 으로 교환한다. */
    public String generateToken(String authorizationCode, String referrer) {
        TossTokenResponse response;
        try {
            response = restClient.post()
                    .uri(generateTokenPath)
                    .body(Map.of("authorizationCode", authorizationCode, "referrer", referrer))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) ->
                            fail(res.getStatusCode(), readBody(res.getBody().readAllBytes()), true))
                    .body(TossTokenResponse.class);
        } catch (RestClientException e) {
            throw new TossAuthException(HttpStatus.UNAUTHORIZED, "TOSS_TOKEN_FAILED",
                    "토스 토큰 교환에 실패했습니다.", e);
        }

        if (response == null || response.accessToken() == null || response.accessToken().isBlank()) {
            throw new TossAuthException(HttpStatus.UNAUTHORIZED, "TOSS_TOKEN_FAILED",
                    "토스 토큰 교환 응답에 accessToken 이 없습니다.");
        }
        return response.accessToken();
    }

    /** ② tossAccessToken 으로 사용자 정보를 조회해 userKey 를 얻는다. */
    public Long fetchUserKey(String tossAccessToken) {
        TossUserInfo info;
        try {
            info = restClient.get()
                    .uri(loginMePath)
                    .header("Authorization", "Bearer " + tossAccessToken)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) ->
                            fail(res.getStatusCode(), readBody(res.getBody().readAllBytes()), false))
                    .body(TossUserInfo.class);
        } catch (RestClientException e) {
            throw new TossAuthException(HttpStatus.INTERNAL_SERVER_ERROR, "TOSS_INTERNAL_ERROR",
                    "토스 사용자 조회에 실패했습니다.", e);
        }

        if (info == null || info.userKey() == null) {
            throw new TossAuthException(HttpStatus.NOT_FOUND, "USER_KEY_NOT_FOUND",
                    "토스에서 userKey 를 받지 못했습니다.");
        }
        return info.userKey();
    }

    /**
     * 토스 실패 응답 → 명세의 (status, errorCode) 매핑.
     * tokenExchange 여부에 따라 매칭되지 않은 코드의 기본값이 달라진다(교환 실패는 인증 거부로 본다).
     */
    private void fail(HttpStatusCode status, String body, boolean tokenExchange) {
        String code = extractErrorCode(body);
        String upper = code == null ? "" : code.toUpperCase(Locale.ROOT);

        if (upper.equals("INVALID_GRANT")) {
            throw new TossAuthException(HttpStatus.BAD_REQUEST, "INVALID_AUTH_CODE",
                    "인가 코드가 만료되었거나 이미 사용되었습니다.");
        }
        if (upper.equals("USER_KEY_NOT_FOUND") || upper.equals("USER_NOT_FOUND")) {
            throw new TossAuthException(HttpStatus.NOT_FOUND, "USER_KEY_NOT_FOUND",
                    "토스에서 사용자 정보를 찾을 수 없습니다.");
        }
        // 내부 오류와 조회 횟수 초과는 같은 코드로 묶어 내보낸다(명세 기준).
        if (upper.equals("INTERNAL_ERROR") || upper.contains("EXCEED") || upper.contains("TOO_MANY")
                || status.is5xxServerError() || status.value() == 429) {
            throw new TossAuthException(HttpStatus.INTERNAL_SERVER_ERROR, "TOSS_INTERNAL_ERROR",
                    "토스 서버 오류로 로그인에 실패했습니다.");
        }

        if (tokenExchange) {
            throw new TossAuthException(HttpStatus.UNAUTHORIZED, "TOSS_TOKEN_FAILED",
                    "토스 토큰 교환이 거부되었습니다.");
        }
        throw new TossAuthException(HttpStatus.UNAUTHORIZED, "TOSS_TOKEN_FAILED",
                "토스 사용자 조회가 거부되었습니다.");
    }

    private String extractErrorCode(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        Matcher matcher = ERROR_CODE.matcher(body);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String readBody(byte[] raw) {
        return raw == null ? null : new String(raw, StandardCharsets.UTF_8);
    }
}

package com.example.demo.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.example.demo.auth.TossAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class TossApiClientTest {

    private static final String TOKEN_PATH = "/api-partner/v1/apps-in-toss/user/oauth2/generate-token";
    private static final String LOGIN_ME_PATH = "/api-partner/v1/apps-in-toss/user/oauth2/login-me";

    private MockRestServiceServer server;
    private TossApiClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://toss.test");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new TossApiClient(builder.build(), TOKEN_PATH, LOGIN_ME_PATH);
    }

    @Test
    void 인가코드를_토스_accessToken_으로_교환한다() {
        server.expect(requestTo("https://toss.test" + TOKEN_PATH))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.authorizationCode").value("code-123"))
                .andExpect(jsonPath("$.referrer").value("DEFAULT"))
                .andRespond(withSuccess("""
                        {"accessToken":"toss-at","refreshToken":"toss-rt","expiresIn":3600,"tokenType":"Bearer"}
                        """, MediaType.APPLICATION_JSON));

        assertThat(client.generateToken("code-123", "DEFAULT")).isEqualTo("toss-at");
        server.verify();
    }

    @Test
    void 인가코드가_만료되면_INVALID_AUTH_CODE() {
        server.expect(requestTo("https://toss.test" + TOKEN_PATH))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {"error":"invalid_grant","error_description":"expired"}
                                """));

        assertThatThrownBy(() -> client.generateToken("expired", "DEFAULT"))
                .isInstanceOf(TossAuthException.class)
                .satisfies(e -> {
                    TossAuthException ex = (TossAuthException) e;
                    assertThat(ex.getErrorCode()).isEqualTo("INVALID_AUTH_CODE");
                    assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                });
    }

    @Test
    void 토큰교환이_거부되면_TOSS_TOKEN_FAILED() {
        server.expect(requestTo("https://toss.test" + TOKEN_PATH))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"errorCode\":\"UNAUTHORIZED_CLIENT\"}"));

        assertThatThrownBy(() -> client.generateToken("code", "DEFAULT"))
                .isInstanceOf(TossAuthException.class)
                .extracting(e -> ((TossAuthException) e).getErrorCode())
                .isEqualTo("TOSS_TOKEN_FAILED");
    }

    @Test
    void login_me_로_userKey_를_조회한다() {
        // 2026-01-02부터 scope 에 user_key 가 추가됐다. 모르는 값이 와도 역직렬화가 깨지면 안 된다.
        server.expect(requestTo("https://toss.test" + LOGIN_ME_PATH))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer toss-at"))
                .andRespond(withSuccess("""
                        {"userKey":90123,"scope":["user_key","some_future_scope"],"unknownField":true}
                        """, MediaType.APPLICATION_JSON));

        assertThat(client.fetchUserKey("toss-at")).isEqualTo(90123L);
        server.verify();
    }

    @Test
    void userKey_조회_실패는_USER_KEY_NOT_FOUND() {
        server.expect(requestTo("https://toss.test" + LOGIN_ME_PATH))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"errorCode\":\"USER_NOT_FOUND\"}"));

        assertThatThrownBy(() -> client.fetchUserKey("toss-at"))
                .isInstanceOf(TossAuthException.class)
                .satisfies(e -> {
                    TossAuthException ex = (TossAuthException) e;
                    assertThat(ex.getErrorCode()).isEqualTo("USER_KEY_NOT_FOUND");
                    assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }

    @Test
    void 토스_5xx_는_TOSS_INTERNAL_ERROR() {
        server.expect(requestTo("https://toss.test" + LOGIN_ME_PATH))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.fetchUserKey("toss-at"))
                .isInstanceOf(TossAuthException.class)
                .satisfies(e -> {
                    TossAuthException ex = (TossAuthException) e;
                    assertThat(ex.getErrorCode()).isEqualTo("TOSS_INTERNAL_ERROR");
                    assertThat(ex.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }
}

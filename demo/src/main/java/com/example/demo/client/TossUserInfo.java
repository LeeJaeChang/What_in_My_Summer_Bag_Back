package com.example.demo.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 앱인토스 사용자 검증 응답. 필드명은 실제 명세에 맞춰 조정한다(@JsonProperty 로 매핑 가능).
 * 명세에 없는 필드는 무시하도록 unknown 은 관대하게 처리한다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TossUserInfo(
        Long userKey,
        String nickname
) {
}

package com.example.demo.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 자체 JWT 발급/검증. 토스 검증으로 얻은 member_id를 subject에 담아 accessToken을 만들고,
 * 이후 요청의 Authorization 헤더에서 member_id를 복원한다.
 *
 * 클라이언트가 member_id를 직접 보내지 않게 하는 것이 핵심 보안 요구사항이라, member_id는
 * 서명된 토큰 안에서만 오간다(위조 시 서명 검증 실패로 걸러진다).
 */
@Component
public class JwtProvider {

    private final SecretKey key;
    private final long expirationSeconds;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-seconds}") long expirationSeconds) {
        // HS256은 최소 256bit(32byte) 키가 필요하다. 짧은 시크릿은 애플리케이션 기동 시점에 바로 실패시킨다.
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret 은 최소 32바이트여야 합니다. 현재: " + keyBytes.length);
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationSeconds = expirationSeconds;
    }

    public String issue(Long memberId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key)
                .compact();
    }

    // 서명·만료를 검증하고 member_id를 복원한다. 실패하면 UnauthorizedException.
    public Long parseMemberId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.parseLong(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("유효하지 않은 accessToken 입니다.", e);
        }
    }
}

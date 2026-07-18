package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * 서비스를 이용하는 사용자. 한 Member는 여러 Trip을 만들 수 있다(1:N).
 *
 * 토스 userKey 로 식별만 하면 되고 유저 정보를 노출하는 화면이 없어 프로필 필드는 두지 않는다.
 */
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "toss_user_key", unique = true, nullable = false)
    private Long tossUserKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Member() {
    }

    public Member(Long tossUserKey) {
        this.tossUserKey = tossUserKey;
    }

    public Long getId() {
        return id;
    }

    public Long getTossUserKey() {
        return tossUserKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

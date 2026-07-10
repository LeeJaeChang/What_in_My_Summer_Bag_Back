package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "toss_user_key", unique = true, nullable = false)
    private String tossUserKey;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    protected User() {
    }

    public User(String userId, String tossUserKey, OffsetDateTime createdAt) {
        this.userId = userId;
        this.tossUserKey = tossUserKey;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getTossUserKey() {
        return tossUserKey;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}

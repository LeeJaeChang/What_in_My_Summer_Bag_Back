package com.example.demo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "checklists")
public class Checklist {

    @Id
    @Column(name = "checklist_id")
    private String checklistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 추천 1건당 체크리스트 1개 (DB UNIQUE 인덱스로도 강제됨)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id", nullable = false, unique = true)
    private Recommendation recommendation;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItem> items = new ArrayList<>();

    protected Checklist() {
    }

    public Checklist(String checklistId, User user, Recommendation recommendation) {
        this.checklistId = checklistId;
        this.user = user;
        this.recommendation = recommendation;
    }

    public String getChecklistId() {
        return checklistId;
    }

    public User getUser() {
        return user;
    }

    public Recommendation getRecommendation() {
        return recommendation;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ChecklistItem> getItems() {
        return items;
    }
}

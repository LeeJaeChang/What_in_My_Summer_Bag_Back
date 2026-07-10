package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @Column(name = "recommendation_id")
    private String recommendationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "region_name", nullable = false)
    private String regionName;

    @Column(name = "travel_date", nullable = false)
    private LocalDate travelDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    // /weather 응답을 그대로 저장 (재조회 없이 재사용)
    @Column(name = "weather_snapshot", columnDefinition = "jsonb", nullable = false)
    private String weatherSnapshot;

    // /recommend가 만든 추천 아이템 원본. POST /checklist에서 이 값을 파싱해 checklist_items로 복제한다.
    @Column(name = "recommended_items", columnDefinition = "jsonb", nullable = false)
    private String recommendedItems;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    protected Recommendation() {
    }

    public Recommendation(String recommendationId, User user, String regionName, LocalDate travelDate,
                          ActivityType activityType, String weatherSnapshot, String recommendedItems,
                          OffsetDateTime createdAt) {
        this.recommendationId = recommendationId;
        this.user = user;
        this.regionName = regionName;
        this.travelDate = travelDate;
        this.activityType = activityType;
        this.weatherSnapshot = weatherSnapshot;
        this.recommendedItems = recommendedItems;
        this.createdAt = createdAt;
    }

    public String getRecommendationId() {
        return recommendationId;
    }

    public User getUser() {
        return user;
    }

    public String getRegionName() {
        return regionName;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public String getWeatherSnapshot() {
        return weatherSnapshot;
    }

    public String getRecommendedItems() {
        return recommendedItems;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}

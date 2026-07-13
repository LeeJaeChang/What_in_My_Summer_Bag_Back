package com.example.demo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 여행 계획. 어디로·언제 가는지와 그 시점의 날씨 정보를 담는다.
 * 한 Trip은 여러 TripActivity(활동)와 여러 PackingItem(준비물)을 가진다(각각 1:N).
 */
@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "travel_tip")
    private String travelTip;

    @Column(name = "temperature_min")
    private Double temperatureMin;

    @Column(name = "temperature_max")
    private Double temperatureMax;

    @Column(name = "precipitation_probability")
    private Integer precipitationProbability;

    // 스펙 원문의 오타(temperaturePrceived)를 temperaturePerceived로 바로잡음
    @Column(name = "temperature_perceived")
    private Double temperaturePerceived;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripActivity> activities = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackingItem> packingItems = new ArrayList<>();

    protected Trip() {
    }

    public Trip(Member member, String destination, LocalDate startDate, LocalDate endDate) {
        this.member = member;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void applyWeather(Double temperatureMin, Double temperatureMax,
                             Integer precipitationProbability, Double temperaturePerceived,
                             String travelTip) {
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
        this.precipitationProbability = precipitationProbability;
        this.temperaturePerceived = temperaturePerceived;
        this.travelTip = travelTip;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getTravelTip() {
        return travelTip;
    }

    public Double getTemperatureMin() {
        return temperatureMin;
    }

    public Double getTemperatureMax() {
        return temperatureMax;
    }

    public Integer getPrecipitationProbability() {
        return precipitationProbability;
    }

    public Double getTemperaturePerceived() {
        return temperaturePerceived;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<TripActivity> getActivities() {
        return activities;
    }

    public List<PackingItem> getPackingItems() {
        return packingItems;
    }
}

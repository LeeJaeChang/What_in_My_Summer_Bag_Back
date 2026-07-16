package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// 서비스가 지원하는 지역 화이트리스트. region_name(한글 지역명)을 PK로,
// OpenWeatherMap 지오코딩 API에 넘길 로마자 질의어를 geocodingQuery에 저장한다.
// 데이터 시딩과 스키마는 Flyway(V2__supported_regions.sql)가 관리한다.
@Entity
@Table(name = "supported_regions")
public class SupportedRegion {

    @Id
    @Column(name = "region_name")
    private String regionName;

    @Column(name = "geocoding_query", nullable = false)
    private String geocodingQuery;

    protected SupportedRegion() {
    }

    public SupportedRegion(String regionName, String geocodingQuery) {
        this.regionName = regionName;
        this.geocodingQuery = geocodingQuery;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getGeocodingQuery() {
        return geocodingQuery;
    }
}

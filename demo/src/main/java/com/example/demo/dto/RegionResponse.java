package com.example.demo.dto;

// GET /regions 목록의 개별 항목. geocoding_query는 서버 내부 전용이라 응답에 포함하지 않는다.
public record RegionResponse(
        String regionName
) {
}

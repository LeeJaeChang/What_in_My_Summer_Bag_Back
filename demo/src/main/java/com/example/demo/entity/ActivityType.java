package com.example.demo.entity;

/**
 * 사용자가 여행에서 선택한 활동 종류.
 * EnumType.STRING으로 저장하며, DB CHECK 제약값과 이름이 그대로 일치한다.
 */
public enum ActivityType {
    SEA,    // 수영 == 바다
    CAMPING,     // 캠핑
    HIKING,      // 등산
    SIGHTSEEING, // 관광
    SHOPPING,    // 쇼핑
    FOOD_TOUR,   // 맛집 탐방
    DRIVING     // 드라이브
}

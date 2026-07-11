package com.example.demo.service;

import java.util.Map;
import java.util.Optional;

// OpenWeatherMap 지오코딩이 한글 지역명은 인식 못 하고 로마자 지명만 인식해서,
// 서비스가 지원하는 지역만 화이트리스트로 관리하며 로마자 질의어로 변환한다.
// 여기 없는 지역명은 INVALID_REGION으로 처리한다.
final class SupportedRegions {

    private static final Map<String, String> KOREAN_TO_QUERY = Map.ofEntries(
            Map.entry("서울", "Seoul,KR"),
            Map.entry("부산", "Busan,KR"),
            Map.entry("강릉", "Gangneung,KR"),
            Map.entry("제주", "Jeju,KR"),
            Map.entry("인천", "Incheon,KR"),
            Map.entry("대구", "Daegu,KR"),
            Map.entry("대전", "Daejeon,KR"),
            Map.entry("광주", "Gwangju,KR"),
            Map.entry("여수", "Yeosu,KR"),
            Map.entry("속초", "Sokcho,KR"),
            Map.entry("경주", "Gyeongju,KR"),
            Map.entry("전주", "Jeonju,KR"),
            Map.entry("춘천", "Chuncheon,KR"),
            Map.entry("포항", "Pohang,KR")
    );

    private SupportedRegions() {
    }

    static Optional<String> toGeocodingQuery(String regionName) {
        return Optional.ofNullable(KOREAN_TO_QUERY.get(regionName));
    }
}

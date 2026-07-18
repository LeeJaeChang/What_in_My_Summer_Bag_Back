package com.example.demo.dto;

import java.util.List;

// GET /trips/{tripId}/packing-items/purchase-list
// 구매할 목록 화면 상단의 '오늘의 날씨' 카드용으로 여행 정보(tripId, destination, activities)와
// 날씨 요약(weather), 날씨 팁(travelTip)을 항목 목록과 함께 내려준다.
// 항상 checked=false 항목만 담기므로 명세의 onlyUnready 쿼리 파라미터는 두지 않는다.
public record PurchaseListResponse(
        Long tripId,
        String destination,
        WeatherResponse weather,
        List<String> activities,
        String travelTip,
        List<PurchaseItemResponse> items
) {
}

package com.example.demo.dto;

import java.util.List;

// GET /trips/{tripId}/packing-items/purchase-list
// 구매할 목록 화면 상단의 '오늘의 날씨' 카드용으로 날씨 요약(weather, 아이콘 포함)과
// 날씨 팁(travelTip)을 항목 목록과 함께 내려준다.
public record PurchaseListResponse(
        WeatherResponse weather,
        String travelTip,
        List<PurchaseItemResponse> packingItems
) {
}

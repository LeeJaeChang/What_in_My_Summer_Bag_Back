package com.example.demo.recommend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * AI가 생성한 준비물 1건. 값 검증은 전부 TripService에서 하고 여기서는 원문 그대로 받는다.
 * category를 PackingCategory 타입으로 두면 AI가 enum에 없는 값을 하나만 뱉어도 응답 전체의
 * 역직렬화가 실패하므로 String으로 받아 TripService에서 ETC로 흡수한다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AiPackingItem(

        String name,
        String category,
        String iconKey,
        String searchKeyword,
        String reason,
        Integer sortOrder

) {
}

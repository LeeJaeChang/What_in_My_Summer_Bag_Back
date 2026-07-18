package com.example.demo.dto;

// 구매할 목록 항목. 체크리스트 응답과 달리 구매 링크 조회용 searchKeyword를 함께 내려준다.
public record PurchaseItemResponse(
        Long id,
        String name,
        String category,
        String reason,
        boolean checked,
        Integer sortOrder,
        String searchKeyword
) {
}

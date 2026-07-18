package com.example.demo.dto;

// 구매할 목록 항목. 체크리스트 응답과 달리 구매 링크 조회용 searchKeyword를 함께 내려준다.
// iconKey(프론트 아이콘 매핑)와 searchKeyword(구매 링크 조회 키)는 역할이 다르다(값이 같을 수 있음).
public record PurchaseItemResponse(
        Long id,
        String name,
        String category,
        String reason,
        String iconKey,
        String searchKeyword,
        boolean checked,
        Integer sortOrder
) {
}

package com.example.demo.dto;

// GET /trips/{tripId}/packing-items/{itemId}/purchase-links
// 항목의 searchKeyword로 ProductLink를 조회해 브랜드 2개의 링크/이미지를 내려준다.
// 매핑은 항상 2개라 2번 필드가 null이 되는 케이스는 없다(스키마도 NOT NULL).
public record PurchaseLinkResponse(
        Long itemId,
        String itemName,
        String searchKeyword,
        String title,
        String brand1Name,
        String link1Url,
        String link1Image,
        String brand2Name,
        String link2Url,
        String link2Image
) {
}

package com.example.demo.entity;

/**
 * 준비물 분류. EnumType.STRING으로 저장하며 DB CHECK 제약값과 이름이 일치한다.
 * TDS 준비물 아이콘 표(TdsPackingIcon)의 category 분류와 값을 맞춘다.
 */
public enum PackingCategory {
    SUN_PROTECTION,  // 자외선 차단 (선크림, 모자, 양산 등)
    WATER,           // 물놀이 (수영복, 아쿠아슈즈, 비치용품 등)
    CLOTHING,        // 의류
    TOILETRIES,      // 세면/위생용품
    HEALTH,          // 건강/상비약
    ELECTRONICS,     // 전자기기
    DOCUMENTS,       // 서류/증명
    ETC              // 기타
}

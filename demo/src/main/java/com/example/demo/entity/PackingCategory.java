package com.example.demo.entity;

/**
 * 준비물 분류. EnumType.STRING으로 저장하며 DB CHECK 제약값과 이름이 일치한다.
 *
 * TODO: 준비물은 추후 AI가 생성하므로, AI 출력 스펙이 확정되면 분류 값도 그에 맞춰 조정한다.
 * (지금은 일반적인 여행 준비물 카테고리로 임시 정의)
 */
public enum PackingCategory {
    CLOTHING,     // 의류
    TOILETRIES,   // 세면/위생용품
    ELECTRONICS,  // 전자기기
    DOCUMENTS,    // 서류/증명
    MEDICINE,     // 상비약
    GEAR,         // 활동 장비
    ETC           // 기타
}

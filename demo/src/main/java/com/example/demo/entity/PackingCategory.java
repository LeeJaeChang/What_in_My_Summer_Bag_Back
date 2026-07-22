package com.example.demo.entity;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

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
    ETC;             // 기타

    /** LLM 프롬프트에 넣을 허용 category 목록. 프롬프트와 enum이 어긋나면 역직렬화가 깨지므로 여기서 생성한다. */
    public static String promptList() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    public static Optional<PackingCategory> from(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(category -> category.name().equalsIgnoreCase(name))
                .findFirst();
    }
}

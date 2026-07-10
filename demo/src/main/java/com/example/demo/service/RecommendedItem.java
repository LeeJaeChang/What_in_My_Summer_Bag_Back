package com.example.demo.service;

import com.fasterxml.jackson.annotation.JsonProperty;

// recommendations.recommended_items(JSONB)에 저장된 형태와 맞춰야 함
// (담당: 김영준의 /recommend 응답 items와 동일 구조)
record RecommendedItem(
        String name,
        @JsonProperty("is_essential") boolean isEssential,
        String reason
) {
}

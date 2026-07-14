package com.example.demo.recommend.client.dto;

import com.example.demo.recommend.domain.PackingCategory;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class OpenAiPackingItemOutput {

    @JsonPropertyDescription("준비물 이름. 예: 선크림, 우산, 등산화")
    public String name;

    @JsonPropertyDescription(
            "준비물 분류. 가능한 값: CLOTHING, ELECTRONICS, "
                    + "TOILETRIES, DOCUMENTS, MEDICINE, ETC"
    )
    public PackingCategory category;

    @JsonPropertyDescription("날씨 또는 여행 활동과 연결된 구체적인 추천 이유")
    public String reason;

    @JsonPropertyDescription("화면 표시 순서. 1부터 시작하는 정수")
    public Integer sortOrder;
}
package com.example.demo.recommend.client.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public class OpenAiRecommendationOutput {

    @JsonPropertyDescription(
            "날씨와 활동 조건을 반영한 짧고 구체적인 한국어 여행 팁"
    )
    public String travelTip;

    @JsonPropertyDescription(
            "여행에 필요한 준비물 목록. 중복 없이 중요한 순서로 작성"
    )
    public List<OpenAiPackingItemOutput> packingItems;
}
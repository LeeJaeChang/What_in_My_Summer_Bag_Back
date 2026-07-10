package com.example.demo.recommend.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RecommendItemResponse(

        @JsonProperty("item_id")
        String itemId,

        String name,

        @JsonProperty("is_essential")
        boolean essential,

        String reason
) {
}
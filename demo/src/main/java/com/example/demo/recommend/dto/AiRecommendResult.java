package com.example.demo.recommend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiRecommendResult(

        String travelTip,

        List<AiPackingItem> packingItems

) {
}

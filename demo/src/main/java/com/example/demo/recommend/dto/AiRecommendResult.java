package com.example.demo.recommend.dto;

import java.util.List;

public record AiRecommendResult(

        String travelTip,

        List<AiPackingItem> packingItems

) {
}
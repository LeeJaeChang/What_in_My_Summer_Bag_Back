package com.example.demo.recommend.dto;

import com.example.demo.recommend.domain.PackingCategory;

public record AiPackingItem(

        String name,

        PackingCategory category,

        String reason,

        Integer sortOrder

) {
}
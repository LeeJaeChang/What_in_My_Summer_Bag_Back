package com.example.demo.recommend.dto;

import com.example.demo.entity.PackingCategory;

public record RecommendItemResponse(

        Long id,
        String name,
        PackingCategory category,
        String iconKey,
        String reason,
        boolean checked,
        Integer sortOrder

) {
}
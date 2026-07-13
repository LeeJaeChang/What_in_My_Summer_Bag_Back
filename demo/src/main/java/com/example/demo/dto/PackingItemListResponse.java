package com.example.demo.dto;

import java.util.List;

// GET /trips/{tripId}/packing-items
public record PackingItemListResponse(
        List<PackingItemResponse> packingItems
) {
}

package com.example.demo.dto;

import java.util.List;

// GET /regions. 화이트리스트라 수가 적어 페이지네이션 없이 전체(또는 q 필터 결과)를 반환한다.
public record RegionListResponse(
        int count,
        List<RegionResponse> regions
) {
}

package com.example.demo.service;

import com.example.demo.dto.RegionListResponse;
import com.example.demo.dto.RegionResponse;
import com.example.demo.entity.SupportedRegion;
import com.example.demo.repository.SupportedRegionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegionService {

    // 검색어 길이 상한. 초과 시 INVALID_QUERY로 응답한다.
    private static final int MAX_QUERY_LENGTH = 20;

    private final SupportedRegionRepository supportedRegionRepository;

    public RegionService(SupportedRegionRepository supportedRegionRepository) {
        this.supportedRegionRepository = supportedRegionRepository;
    }

    // GET /regions. q 미전달/공백이면 전체, 있으면 region_name 부분일치. 히트 없으면 빈 목록(에러 아님).
    @Transactional(readOnly = true)
    public RegionListResponse listRegions(String q) {
        List<SupportedRegion> regions = (q == null || q.isBlank())
                ? supportedRegionRepository.findAllByOrderByRegionNameAsc()
                : searchRegions(q.trim());

        List<RegionResponse> items = regions.stream()
                .map(region -> new RegionResponse(region.getRegionName()))
                .toList();
        return new RegionListResponse(items.size(), items);
    }

    private List<SupportedRegion> searchRegions(String q) {
        if (q.length() > MAX_QUERY_LENGTH) {
            throw new InvalidQueryException("검색어는 최대 %d자까지 가능합니다.".formatted(MAX_QUERY_LENGTH));
        }
        return supportedRegionRepository.findByRegionNameContainingIgnoreCaseOrderByRegionNameAsc(q);
    }
}

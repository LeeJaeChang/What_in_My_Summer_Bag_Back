package com.example.demo.controller;

import com.example.demo.dto.RegionListResponse;
import com.example.demo.service.RegionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    // 지원 지역 목록 조회. q로 region_name 부분일치 검색(미전달 시 전체).
    @GetMapping
    public ResponseEntity<RegionListResponse> listRegions(
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(regionService.listRegions(q));
    }
}

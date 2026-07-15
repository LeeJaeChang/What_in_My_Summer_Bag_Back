package com.example.demo.repository;

import com.example.demo.entity.SupportedRegion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportedRegionRepository extends JpaRepository<SupportedRegion, String> {

    // GET /regions: 가나다순 전체 조회
    List<SupportedRegion> findAllByOrderByRegionNameAsc();

    // GET /regions?q=...: region_name 부분일치(대소문자 무시) + 가나다순
    List<SupportedRegion> findByRegionNameContainingIgnoreCaseOrderByRegionNameAsc(String q);
}

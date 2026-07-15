package com.example.demo.repository;

import com.example.demo.entity.SupportedRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportedRegionRepository extends JpaRepository<SupportedRegion, String> {
}

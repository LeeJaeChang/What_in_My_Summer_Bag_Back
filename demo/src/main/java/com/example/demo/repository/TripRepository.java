package com.example.demo.repository;

import com.example.demo.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Page<Trip> findByMemberId(Long memberId, Pageable pageable);
}

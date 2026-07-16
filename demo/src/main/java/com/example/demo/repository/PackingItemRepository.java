package com.example.demo.repository;

import com.example.demo.entity.PackingItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackingItemRepository extends JpaRepository<PackingItem, Long> {

    List<PackingItem> findByTripIdOrderBySortOrderAsc(Long tripId);

    // 구매할 목록: 아직 체크되지 않은(준비되지 않은) 준비물만
    List<PackingItem> findByTripIdAndCheckedFalseOrderBySortOrderAsc(Long tripId);
}

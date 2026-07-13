package com.example.demo.repository;

import com.example.demo.entity.PackingItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackingItemRepository extends JpaRepository<PackingItem, Long> {
}

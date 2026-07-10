package com.example.demo.repository;

import com.example.demo.entity.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChecklistRepository extends JpaRepository<Checklist, String> {
}

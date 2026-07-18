package com.example.demo.repository;

import com.example.demo.entity.ProductLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLinkRepository extends JpaRepository<ProductLink, String> {
}

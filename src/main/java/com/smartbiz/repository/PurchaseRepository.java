package com.smartbiz.repository;

import com.smartbiz.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByBusinessIdOrderByCreatedAtDesc(Long businessId);
    long countByProductId(Long productId);
}

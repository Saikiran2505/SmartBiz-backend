package com.smartbiz.repository;

import com.smartbiz.entity.StockAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {
    List<StockAdjustment> findByBusinessIdOrderByCreatedAtDesc(Long businessId);
    long countByProductId(Long productId);
    void deleteByProductId(Long productId);
}

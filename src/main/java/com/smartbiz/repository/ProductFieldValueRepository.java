package com.smartbiz.repository;

import com.smartbiz.entity.ProductFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductFieldValueRepository extends JpaRepository<ProductFieldValue, Long> {
    List<ProductFieldValue> findByProductId(Long productId);
    Optional<ProductFieldValue> findByProductIdAndFieldId(Long productId, Long fieldId);
    void deleteByProductId(Long productId);
    void deleteByFieldId(Long fieldId);
}

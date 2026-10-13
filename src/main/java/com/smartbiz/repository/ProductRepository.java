package com.smartbiz.repository;

import com.smartbiz.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBusinessIdOrderByNameAsc(Long businessId);
    Optional<Product> findByIdAndBusinessId(Long id, Long businessId);
    boolean existsByNameAndBusinessId(String name, Long businessId);
    long countByCategoryId(Long categoryId);
}

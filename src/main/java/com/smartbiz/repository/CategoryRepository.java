package com.smartbiz.repository;

import com.smartbiz.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByBusinessIdOrderByNameAsc(Long businessId);
    Optional<Category> findByIdAndBusinessId(Long id, Long businessId);
    boolean existsByNameAndBusinessId(String name, Long businessId);
}

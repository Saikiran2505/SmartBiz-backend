package com.smartbiz.repository;

import com.smartbiz.entity.CategoryField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryFieldRepository extends JpaRepository<CategoryField, Long> {
    List<CategoryField> findByCategoryIdAndBusinessId(Long categoryId, Long businessId);
    Optional<CategoryField> findByIdAndBusinessId(Long id, Long businessId);
    boolean existsByFieldNameAndCategoryIdAndBusinessId(String fieldName, Long categoryId, Long businessId);
}

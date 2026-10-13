package com.smartbiz.repository;

import com.smartbiz.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    List<ExpenseCategory> findByBusinessIdOrderByNameAsc(Long businessId);
    Optional<ExpenseCategory> findByIdAndBusinessId(Long id, Long businessId);
    boolean existsByNameAndBusinessId(String name, Long businessId);
}

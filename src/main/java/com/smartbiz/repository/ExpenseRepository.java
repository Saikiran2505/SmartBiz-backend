package com.smartbiz.repository;

import com.smartbiz.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByBusinessIdOrderByCreatedAtDesc(Long businessId);
    Optional<Expense> findByIdAndBusinessId(Long id, Long businessId);
    long countByCategoryId(Long categoryId);
}

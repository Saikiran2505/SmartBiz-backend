package com.smartbiz.service;

import com.smartbiz.dto.ExpenseDto;
import com.smartbiz.entity.Business;
import com.smartbiz.entity.Expense;
import com.smartbiz.entity.ExpenseCategory;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.ExpenseCategoryRepository;
import com.smartbiz.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final BusinessRepository businessRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          ExpenseCategoryRepository expenseCategoryRepository,
                          BusinessRepository businessRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.businessRepository = businessRepository;
    }

    @Transactional
    public ExpenseDto createExpense(Long businessId, ExpenseDto dto) {
        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("Expense category ID is required.");
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero.");
        }

        ExpenseCategory category = expenseCategoryRepository.findByIdAndBusinessId(dto.getCategoryId(), businessId)
                .orElseThrow(() -> new IllegalArgumentException("Expense category not found."));

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found."));

        Expense expense = new Expense(
                category,
                business,
                dto.getAmount(),
                dto.getDescription() != null ? dto.getDescription().trim() : null,
                business.getWorkingDate()
        );

        Expense saved = expenseRepository.save(expense);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ExpenseDto> getExpenses(Long businessId) {
        List<Expense> expenses = expenseRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        return expenses.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public ExpenseDto updateExpense(Long businessId, Long expenseId, ExpenseDto dto) {
        Expense expense = expenseRepository.findByIdAndBusinessId(expenseId, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Expense record not found."));

        if (dto.getCategoryId() != null) {
            ExpenseCategory category = expenseCategoryRepository.findByIdAndBusinessId(dto.getCategoryId(), businessId)
                    .orElseThrow(() -> new IllegalArgumentException("Expense category not found."));
            expense.setCategory(category);
        }

        if (dto.getAmount() != null) {
            if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Expense amount must be greater than zero.");
            }
            expense.setAmount(dto.getAmount());
        }

        if (dto.getDescription() != null) {
            expense.setDescription(dto.getDescription().trim());
        }

        Expense updated = expenseRepository.save(expense);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteExpense(Long businessId, Long expenseId) {
        Expense expense = expenseRepository.findByIdAndBusinessId(expenseId, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Expense record not found."));
        expenseRepository.delete(expense);
    }

    private ExpenseDto mapToDto(Expense expense) {
        return new ExpenseDto(
                expense.getId(),
                expense.getCategory().getId(),
                expense.getCategory().getName(),
                expense.getAmount(),
                expense.getDescription(),
                expense.getWorkingDate(),
                expense.getCreatedAt()
        );
    }
}

package com.smartbiz.service;

import com.smartbiz.dto.ExpenseCategoryDto;
import com.smartbiz.entity.Business;
import com.smartbiz.entity.ExpenseCategory;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.ExpenseCategoryRepository;
import com.smartbiz.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final BusinessRepository businessRepository;
    private final ExpenseRepository expenseRepository;

    public ExpenseCategoryService(ExpenseCategoryRepository expenseCategoryRepository,
                                  BusinessRepository businessRepository,
                                  ExpenseRepository expenseRepository) {
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.businessRepository = businessRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    public ExpenseCategoryDto createCategory(Long businessId, ExpenseCategoryDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required.");
        }

        String name = dto.getName().trim();
        if (expenseCategoryRepository.existsByNameAndBusinessId(name, businessId)) {
            throw new IllegalArgumentException("Expense category '" + name + "' already exists.");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found."));

        ExpenseCategory category = new ExpenseCategory(
                name,
                dto.getDescription() != null ? dto.getDescription().trim() : null,
                business
        );

        ExpenseCategory saved = expenseCategoryRepository.save(category);
        return mapToDto(saved);
    }

    @Transactional
    public List<ExpenseCategoryDto> getCategories(Long businessId) {
        List<ExpenseCategory> list = expenseCategoryRepository.findByBusinessIdOrderByNameAsc(businessId);
        
        // Auto-seed default expense categories if business has none yet
        if (list.isEmpty()) {
            Business business = businessRepository.findById(businessId).orElse(null);
            if (business != null) {
                List<String> defaults = Arrays.asList("Rent", "Electricity", "Salary", "Transport", "Maintenance", "Other");
                for (String defaultName : defaults) {
                    expenseCategoryRepository.save(new ExpenseCategory(defaultName, "Default expense category", business));
                }
                list = expenseCategoryRepository.findByBusinessIdOrderByNameAsc(businessId);
            }
        }

        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteCategory(Long businessId, Long categoryId) {
        ExpenseCategory category = expenseCategoryRepository.findByIdAndBusinessId(categoryId, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Expense category not found."));

        long count = expenseRepository.countByCategoryId(categoryId);
        if (count > 0) {
            throw new IllegalArgumentException("Cannot delete expense category. It has " + count + " associated expense records.");
        }

        expenseCategoryRepository.delete(category);
    }

    private ExpenseCategoryDto mapToDto(ExpenseCategory category) {
        return new ExpenseCategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}

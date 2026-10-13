package com.smartbiz.controller;

import com.smartbiz.dto.ExpenseCategoryDto;
import com.smartbiz.security.CustomUserDetails;
import com.smartbiz.service.ExpenseCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expense-categories")
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    public ExpenseCategoryController(ExpenseCategoryService expenseCategoryService) {
        this.expenseCategoryService = expenseCategoryService;
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestBody ExpenseCategoryDto dto) {
        try {
            ExpenseCategoryDto created = expenseCategoryService.createCategory(userDetails.getBusinessId(), dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<ExpenseCategoryDto>> getCategories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ExpenseCategoryDto> list = expenseCategoryService.getCategories(userDetails.getBusinessId());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable Long id) {
        try {
            expenseCategoryService.deleteCategory(userDetails.getBusinessId(), id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Expense category deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

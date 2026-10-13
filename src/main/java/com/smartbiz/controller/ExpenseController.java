package com.smartbiz.controller;

import com.smartbiz.dto.ExpenseDto;
import com.smartbiz.security.CustomUserDetails;
import com.smartbiz.service.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<?> createExpense(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestBody ExpenseDto dto) {
        try {
            ExpenseDto created = expenseService.createExpense(userDetails.getBusinessId(), dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getExpenses(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ExpenseDto> expenses = expenseService.getExpenses(userDetails.getBusinessId());
        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable Long id,
                                            @RequestBody ExpenseDto dto) {
        try {
            ExpenseDto updated = expenseService.updateExpense(userDetails.getBusinessId(), id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable Long id) {
        try {
            expenseService.deleteExpense(userDetails.getBusinessId(), id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Expense deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

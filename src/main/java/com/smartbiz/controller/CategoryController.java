package com.smartbiz.controller;

import com.smartbiz.dto.CategoryDto;
import com.smartbiz.security.CustomUserDetails;
import com.smartbiz.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestBody CategoryDto dto) {
        try {
            CategoryDto created = categoryService.createCategory(userDetails.getBusinessId(), dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CategoryDto> list = categoryService.getCategories(userDetails.getBusinessId());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable Long id,
                                             @RequestBody CategoryDto dto) {
        try {
            CategoryDto updated = categoryService.updateCategory(userDetails.getBusinessId(), id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable Long id) {
        try {
            categoryService.deleteCategory(userDetails.getBusinessId(), id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Category deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

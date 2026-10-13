package com.smartbiz.controller;

import com.smartbiz.dto.CategoryFieldDto;
import com.smartbiz.security.CustomUserDetails;
import com.smartbiz.service.CategoryFieldService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories/{categoryId}/fields")
public class CategoryFieldController {

    private final CategoryFieldService categoryFieldService;

    public CategoryFieldController(CategoryFieldService categoryFieldService) {
        this.categoryFieldService = categoryFieldService;
    }

    @PostMapping
    public ResponseEntity<?> addFieldToCategory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @PathVariable Long categoryId,
                                                 @RequestBody CategoryFieldDto dto) {
        try {
            CategoryFieldDto created = categoryFieldService.addFieldToCategory(userDetails.getBusinessId(), categoryId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoryFieldDto>> getFieldsForCategory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                         @PathVariable Long categoryId) {
        List<CategoryFieldDto> fields = categoryFieldService.getFieldsForCategory(userDetails.getBusinessId(), categoryId);
        return ResponseEntity.ok(fields);
    }

    @PutMapping("/{fieldId}")
    public ResponseEntity<?> updateField(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @PathVariable Long categoryId,
                                         @PathVariable Long fieldId,
                                         @RequestBody CategoryFieldDto dto) {
        try {
            CategoryFieldDto updated = categoryFieldService.updateField(userDetails.getBusinessId(), categoryId, fieldId, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{fieldId}")
    public ResponseEntity<?> deleteField(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @PathVariable Long categoryId,
                                         @PathVariable Long fieldId) {
        try {
            categoryFieldService.deleteField(userDetails.getBusinessId(), categoryId, fieldId);
            Map<String, String> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Custom field deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

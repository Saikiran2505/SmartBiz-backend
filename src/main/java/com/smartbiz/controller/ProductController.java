package com.smartbiz.controller;

import com.smartbiz.dto.ProductDto;
import com.smartbiz.security.CustomUserDetails;
import com.smartbiz.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestBody ProductDto dto) {
        try {
            ProductDto created = productService.createProduct(userDetails.getBusinessId(), dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ProductDto> list = productService.getProducts(userDetails.getBusinessId());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable Long id) {
        try {
            ProductDto dto = productService.getProductById(userDetails.getBusinessId(), id);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable Long id,
                                            @RequestBody ProductDto dto) {
        try {
            ProductDto updated = productService.updateProduct(userDetails.getBusinessId(), id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable Long id) {
        try {
            productService.deleteProduct(userDetails.getBusinessId(), id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Product deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

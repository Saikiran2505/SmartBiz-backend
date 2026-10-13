package com.smartbiz.controller;

import com.smartbiz.dto.SaleDto;
import com.smartbiz.security.CustomUserDetails;
import com.smartbiz.service.SaleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<?> recordSale(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @RequestBody SaleDto dto) {
        try {
            SaleDto created = saleService.recordSale(userDetails.getBusinessId(), dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<SaleDto>> getSales(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<SaleDto> sales = saleService.getSales(userDetails.getBusinessId());
        return ResponseEntity.ok(sales);
    }
}

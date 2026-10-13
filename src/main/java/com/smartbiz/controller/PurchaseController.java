package com.smartbiz.controller;

import com.smartbiz.dto.PurchaseDto;
import com.smartbiz.security.CustomUserDetails;
import com.smartbiz.service.PurchaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public ResponseEntity<?> recordPurchase(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestBody PurchaseDto dto) {
        try {
            PurchaseDto created = purchaseService.recordPurchase(userDetails.getBusinessId(), dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<PurchaseDto>> getPurchases(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PurchaseDto> purchases = purchaseService.getPurchases(userDetails.getBusinessId());
        return ResponseEntity.ok(purchases);
    }
}

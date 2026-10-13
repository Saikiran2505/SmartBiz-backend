package com.smartbiz.controller;

import com.smartbiz.dto.InventoryItemDto;
import com.smartbiz.dto.StockAdjustmentDto;
import com.smartbiz.security.CustomUserDetails;
import com.smartbiz.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<InventoryItemDto>> getInventory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<InventoryItemDto> items = inventoryService.getInventory(userDetails.getBusinessId());
        return ResponseEntity.ok(items);
    }

    @PostMapping("/adjust")
    public ResponseEntity<?> adjustStock(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @RequestBody StockAdjustmentDto dto) {
        try {
            InventoryItemDto updatedItem = inventoryService.adjustStock(userDetails.getBusinessId(), dto);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<StockAdjustmentDto>> getAdjustmentHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<StockAdjustmentDto> history = inventoryService.getAdjustmentHistory(userDetails.getBusinessId());
        return ResponseEntity.ok(history);
    }
}

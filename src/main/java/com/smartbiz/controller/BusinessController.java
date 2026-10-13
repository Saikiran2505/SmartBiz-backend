package com.smartbiz.controller;

import com.smartbiz.dto.WorkingDateDto;
import com.smartbiz.security.CustomUserDetails;
import com.smartbiz.service.BusinessService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/business")
public class BusinessController {

    private final BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @GetMapping("/working-date")
    public ResponseEntity<?> getWorkingDate(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            WorkingDateDto dto = businessService.getWorkingDate(userDetails.getBusinessId());
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/working-date")
    public ResponseEntity<?> updateWorkingDate(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestBody WorkingDateDto request) {
        try {
            WorkingDateDto dto = businessService.updateWorkingDate(
                    userDetails.getBusinessId(),
                    request.getWorkingDate()
            );
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

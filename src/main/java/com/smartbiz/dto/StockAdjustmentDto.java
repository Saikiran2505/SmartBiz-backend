package com.smartbiz.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockAdjustmentDto {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantityChange;
    private String reason;
    private LocalDate workingDate;
    private LocalDateTime createdAt;

    public StockAdjustmentDto() {
    }

    public StockAdjustmentDto(Long id, Long productId, String productName, Integer quantityChange, String reason, LocalDate workingDate, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantityChange = quantityChange;
        this.reason = reason;
        this.workingDate = workingDate;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(LocalDate workingDate) {
        this.workingDate = workingDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

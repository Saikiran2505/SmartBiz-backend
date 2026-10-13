package com.smartbiz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseDto {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantityPurchased;
    private BigDecimal costPrice;
    private BigDecimal totalAmount;
    private LocalDate workingDate;
    private LocalDateTime createdAt;
    private Boolean updateProductCostPrice;

    public PurchaseDto() {
    }

    public PurchaseDto(Long id, Long productId, String productName, Integer quantityPurchased, BigDecimal costPrice, BigDecimal totalAmount, LocalDate workingDate, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantityPurchased = quantityPurchased;
        this.costPrice = costPrice;
        this.totalAmount = totalAmount;
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

    public Integer getQuantityPurchased() {
        return quantityPurchased;
    }

    public void setQuantityPurchased(Integer quantityPurchased) {
        this.quantityPurchased = quantityPurchased;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public Boolean getUpdateProductCostPrice() {
        return updateProductCostPrice;
    }

    public void setUpdateProductCostPrice(Boolean updateProductCostPrice) {
        this.updateProductCostPrice = updateProductCostPrice;
    }
}

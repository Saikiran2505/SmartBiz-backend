package com.smartbiz.dto;

import java.math.BigDecimal;

public class ProductProfitDto {
    private Long productId;
    private String productName;
    private Integer quantitySold;
    private BigDecimal revenue;
    private BigDecimal profit;

    public ProductProfitDto() {
    }

    public ProductProfitDto(Long productId, String productName, Integer quantitySold, BigDecimal revenue, BigDecimal profit) {
        this.productId = productId;
        this.productName = productName;
        this.quantitySold = quantitySold;
        this.revenue = revenue;
        this.profit = profit;
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

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Integer quantitySold) {
        this.quantitySold = quantitySold;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }
}

package com.smartbiz.dto;

import java.math.BigDecimal;

public class CategorySummaryDto {
    private String categoryName;
    private BigDecimal amount;
    private Long itemCount;

    public CategorySummaryDto() {
    }

    public CategorySummaryDto(String categoryName, BigDecimal amount, Long itemCount) {
        this.categoryName = categoryName;
        this.amount = amount;
        this.itemCount = itemCount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getItemCount() {
        return itemCount;
    }

    public void setItemCount(Long itemCount) {
        this.itemCount = itemCount;
    }
}

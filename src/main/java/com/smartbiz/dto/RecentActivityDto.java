package com.smartbiz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RecentActivityDto {
    private String type; // "SALE", "PURCHASE", "EXPENSE", "STOCK_ADJUSTMENT"
    private String title;
    private String description;
    private BigDecimal amount;
    private LocalDate workingDate;
    private LocalDateTime timestamp;

    public RecentActivityDto() {
    }

    public RecentActivityDto(String type, String title, String description, BigDecimal amount, LocalDate workingDate, LocalDateTime timestamp) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.workingDate = workingDate;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(LocalDate workingDate) {
        this.workingDate = workingDate;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

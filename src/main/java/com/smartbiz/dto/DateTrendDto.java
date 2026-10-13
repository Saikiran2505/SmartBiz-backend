package com.smartbiz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DateTrendDto {
    private LocalDate date;
    private BigDecimal amount;

    public DateTrendDto() {
    }

    public DateTrendDto(LocalDate date, BigDecimal amount) {
        this.date = date;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

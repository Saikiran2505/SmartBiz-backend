package com.smartbiz.dto;

import java.math.BigDecimal;

public class ProfitReportSummaryDto {
    private BigDecimal revenue = BigDecimal.ZERO;
    private BigDecimal productCost = BigDecimal.ZERO;
    private BigDecimal grossProfit = BigDecimal.ZERO;
    private BigDecimal expenses = BigDecimal.ZERO;
    private BigDecimal netProfit = BigDecimal.ZERO;

    public ProfitReportSummaryDto() {
    }

    public ProfitReportSummaryDto(BigDecimal revenue, BigDecimal productCost, BigDecimal grossProfit, BigDecimal expenses, BigDecimal netProfit) {
        this.revenue = revenue;
        this.productCost = productCost;
        this.grossProfit = grossProfit;
        this.expenses = expenses;
        this.netProfit = netProfit;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getProductCost() {
        return productCost;
    }

    public void setProductCost(BigDecimal productCost) {
        this.productCost = productCost;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }
}

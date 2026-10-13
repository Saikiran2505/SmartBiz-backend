package com.smartbiz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnalysisSummaryDto {
    private String filterType;
    private LocalDate startDate;
    private LocalDate endDate;

    // Sales Analysis
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private Integer salesCount = 0;
    private List<DateTrendDto> salesTrend = new ArrayList<>();

    // Product Analysis
    private List<ProductProfitDto> topProductsByQuantity = new ArrayList<>();
    private List<ProductProfitDto> topProductsByRevenue = new ArrayList<>();
    private List<ProductProfitDto> topProductsByProfit = new ArrayList<>();

    // Category Analysis
    private List<CategorySummaryDto> revenueByCategory = new ArrayList<>();

    // Purchase Analysis
    private BigDecimal totalPurchaseAmount = BigDecimal.ZERO;
    private Integer purchaseCount = 0;
    private List<DateTrendDto> purchaseTrend = new ArrayList<>();

    // Expense Analysis
    private BigDecimal totalExpenseAmount = BigDecimal.ZERO;
    private Integer expenseCount = 0;
    private List<DateTrendDto> expenseTrend = new ArrayList<>();
    private List<CategorySummaryDto> expensesByCategory = new ArrayList<>();

    // Profit Analysis
    private BigDecimal salesRevenue = BigDecimal.ZERO;
    private BigDecimal totalProductCost = BigDecimal.ZERO;
    private BigDecimal grossProfit = BigDecimal.ZERO;
    private BigDecimal totalExpenses = BigDecimal.ZERO;
    private BigDecimal netProfit = BigDecimal.ZERO;

    // Inventory Analysis
    private Integer totalCurrentStock = 0;
    private Integer lowStockCount = 0;
    private Integer outOfStockCount = 0;
    private Integer inStockCount = 0;
    private List<InventoryItemDto> lowStockProducts = new ArrayList<>();
    private List<InventoryItemDto> outOfStockProducts = new ArrayList<>();

    public AnalysisSummaryDto() {
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Integer getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
    }

    public List<DateTrendDto> getSalesTrend() {
        return salesTrend;
    }

    public void setSalesTrend(List<DateTrendDto> salesTrend) {
        this.salesTrend = salesTrend;
    }

    public List<ProductProfitDto> getTopProductsByQuantity() {
        return topProductsByQuantity;
    }

    public void setTopProductsByQuantity(List<ProductProfitDto> topProductsByQuantity) {
        this.topProductsByQuantity = topProductsByQuantity;
    }

    public List<ProductProfitDto> getTopProductsByRevenue() {
        return topProductsByRevenue;
    }

    public void setTopProductsByRevenue(List<ProductProfitDto> topProductsByRevenue) {
        this.topProductsByRevenue = topProductsByRevenue;
    }

    public List<ProductProfitDto> getTopProductsByProfit() {
        return topProductsByProfit;
    }

    public void setTopProductsByProfit(List<ProductProfitDto> topProductsByProfit) {
        this.topProductsByProfit = topProductsByProfit;
    }

    public List<CategorySummaryDto> getRevenueByCategory() {
        return revenueByCategory;
    }

    public void setRevenueByCategory(List<CategorySummaryDto> revenueByCategory) {
        this.revenueByCategory = revenueByCategory;
    }

    public BigDecimal getTotalPurchaseAmount() {
        return totalPurchaseAmount;
    }

    public void setTotalPurchaseAmount(BigDecimal totalPurchaseAmount) {
        this.totalPurchaseAmount = totalPurchaseAmount;
    }

    public Integer getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(Integer purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    public List<DateTrendDto> getPurchaseTrend() {
        return purchaseTrend;
    }

    public void setPurchaseTrend(List<DateTrendDto> purchaseTrend) {
        this.purchaseTrend = purchaseTrend;
    }

    public BigDecimal getTotalExpenseAmount() {
        return totalExpenseAmount;
    }

    public void setTotalExpenseAmount(BigDecimal totalExpenseAmount) {
        this.totalExpenseAmount = totalExpenseAmount;
    }

    public Integer getExpenseCount() {
        return expenseCount;
    }

    public void setExpenseCount(Integer expenseCount) {
        this.expenseCount = expenseCount;
    }

    public List<DateTrendDto> getExpenseTrend() {
        return expenseTrend;
    }

    public void setExpenseTrend(List<DateTrendDto> expenseTrend) {
        this.expenseTrend = expenseTrend;
    }

    public List<CategorySummaryDto> getExpensesByCategory() {
        return expensesByCategory;
    }

    public void setExpensesByCategory(List<CategorySummaryDto> expensesByCategory) {
        this.expensesByCategory = expensesByCategory;
    }

    public BigDecimal getSalesRevenue() {
        return salesRevenue;
    }

    public void setSalesRevenue(BigDecimal salesRevenue) {
        this.salesRevenue = salesRevenue;
    }

    public BigDecimal getTotalProductCost() {
        return totalProductCost;
    }

    public void setTotalProductCost(BigDecimal totalProductCost) {
        this.totalProductCost = totalProductCost;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }

    public Integer getTotalCurrentStock() {
        return totalCurrentStock;
    }

    public void setTotalCurrentStock(Integer totalCurrentStock) {
        this.totalCurrentStock = totalCurrentStock;
    }

    public Integer getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(Integer lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public Integer getOutOfStockCount() {
        return outOfStockCount;
    }

    public void setOutOfStockCount(Integer outOfStockCount) {
        this.outOfStockCount = outOfStockCount;
    }

    public Integer getInStockCount() {
        return inStockCount;
    }

    public void setInStockCount(Integer inStockCount) {
        this.inStockCount = inStockCount;
    }

    public List<InventoryItemDto> getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(List<InventoryItemDto> lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public List<InventoryItemDto> getOutOfStockProducts() {
        return outOfStockProducts;
    }

    public void setOutOfStockProducts(List<InventoryItemDto> outOfStockProducts) {
        this.outOfStockProducts = outOfStockProducts;
    }
}

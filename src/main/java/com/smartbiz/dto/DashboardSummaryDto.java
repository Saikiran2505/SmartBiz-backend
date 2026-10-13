package com.smartbiz.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardSummaryDto {
    private BigDecimal todaySales = BigDecimal.ZERO;
    private BigDecimal monthSales = BigDecimal.ZERO;
    private Integer lowStockCount = 0;
    private BigDecimal netProfitMonth = BigDecimal.ZERO;

    private List<DailySalesDto> salesTrend = new ArrayList<>();
    private List<TopProductDto> topProducts = new ArrayList<>();
    private List<InventoryItemDto> lowStockAlerts = new ArrayList<>();
    private List<RecentActivityDto> recentActivities = new ArrayList<>();

    public DashboardSummaryDto() {
    }

    public BigDecimal getTodaySales() {
        return todaySales;
    }

    public void setTodaySales(BigDecimal todaySales) {
        this.todaySales = todaySales;
    }

    public BigDecimal getMonthSales() {
        return monthSales;
    }

    public void setMonthSales(BigDecimal monthSales) {
        this.monthSales = monthSales;
    }

    public Integer getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(Integer lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public BigDecimal getNetProfitMonth() {
        return netProfitMonth;
    }

    public void setNetProfitMonth(BigDecimal netProfitMonth) {
        this.netProfitMonth = netProfitMonth;
    }

    public List<DailySalesDto> getSalesTrend() {
        return salesTrend;
    }

    public void setSalesTrend(List<DailySalesDto> salesTrend) {
        this.salesTrend = salesTrend;
    }

    public List<TopProductDto> getTopProducts() {
        return topProducts;
    }

    public void setTopProducts(List<TopProductDto> topProducts) {
        this.topProducts = topProducts;
    }

    public List<InventoryItemDto> getLowStockAlerts() {
        return lowStockAlerts;
    }

    public void setLowStockAlerts(List<InventoryItemDto> lowStockAlerts) {
        this.lowStockAlerts = lowStockAlerts;
    }

    public List<RecentActivityDto> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<RecentActivityDto> recentActivities) {
        this.recentActivities = recentActivities;
    }
}

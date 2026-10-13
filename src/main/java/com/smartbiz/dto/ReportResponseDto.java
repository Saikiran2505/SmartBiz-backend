package com.smartbiz.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportResponseDto {
    private String reportType;
    private String filterType;
    private LocalDate startDate;
    private LocalDate endDate;

    private List<SaleDto> salesReport = new ArrayList<>();
    private List<PurchaseDto> purchaseReport = new ArrayList<>();
    private List<ExpenseDto> expenseReport = new ArrayList<>();
    private ProfitReportSummaryDto profitReport;
    private List<InventoryItemDto> inventoryReport = new ArrayList<>();

    public ReportResponseDto() {
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
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

    public List<SaleDto> getSalesReport() {
        return salesReport;
    }

    public void setSalesReport(List<SaleDto> salesReport) {
        this.salesReport = salesReport;
    }

    public List<PurchaseDto> getPurchaseReport() {
        return purchaseReport;
    }

    public void setPurchaseReport(List<PurchaseDto> purchaseReport) {
        this.purchaseReport = purchaseReport;
    }

    public List<ExpenseDto> getExpenseReport() {
        return expenseReport;
    }

    public void setExpenseReport(List<ExpenseDto> expenseReport) {
        this.expenseReport = expenseReport;
    }

    public ProfitReportSummaryDto getProfitReport() {
        return profitReport;
    }

    public void setProfitReport(ProfitReportSummaryDto profitReport) {
        this.profitReport = profitReport;
    }

    public List<InventoryItemDto> getInventoryReport() {
        return inventoryReport;
    }

    public void setInventoryReport(List<InventoryItemDto> inventoryReport) {
        this.inventoryReport = inventoryReport;
    }
}

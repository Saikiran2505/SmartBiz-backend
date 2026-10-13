package com.smartbiz.service;

import com.smartbiz.dto.*;
import com.smartbiz.entity.*;
import com.smartbiz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final PurchaseRepository purchaseRepository;
    private final ExpenseRepository expenseRepository;

    public ReportService(BusinessRepository businessRepository,
                         ProductRepository productRepository,
                         SaleRepository saleRepository,
                         PurchaseRepository purchaseRepository,
                         ExpenseRepository expenseRepository) {
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.purchaseRepository = purchaseRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public ReportResponseDto generateReport(Long businessId, String reportType, String filterType, LocalDate customStart, LocalDate customEnd) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found."));

        LocalDate workingDate = business.getWorkingDate();
        LocalDate startDate;
        LocalDate endDate;

        String fType = (filterType != null && !filterType.trim().isEmpty()) ? filterType.trim().toUpperCase() : "THIS_MONTH";

        switch (fType) {
            case "TODAY":
                startDate = workingDate;
                endDate = workingDate;
                break;
            case "THIS_WEEK":
                startDate = workingDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                endDate = workingDate;
                break;
            case "THIS_YEAR":
                startDate = workingDate.withDayOfYear(1);
                endDate = workingDate;
                break;
            case "CUSTOM":
                startDate = customStart != null ? customStart : workingDate.withDayOfMonth(1);
                endDate = customEnd != null ? customEnd : workingDate;
                break;
            case "THIS_MONTH":
            default:
                fType = "THIS_MONTH";
                startDate = workingDate.withDayOfMonth(1);
                endDate = workingDate;
                break;
        }

        String rType = (reportType != null && !reportType.trim().isEmpty()) ? reportType.trim().toUpperCase() : "SALES";

        ReportResponseDto response = new ReportResponseDto();
        response.setReportType(rType);
        response.setFilterType(fType);
        response.setStartDate(startDate);
        response.setEndDate(endDate);

        final LocalDate finalStart = startDate;
        final LocalDate finalEnd = endDate;

        switch (rType) {
            case "PURCHASE": {
                List<Purchase> purchases = purchaseRepository.findByBusinessIdOrderByCreatedAtDesc(businessId).stream()
                        .filter(p -> !p.getWorkingDate().isBefore(finalStart) && !p.getWorkingDate().isAfter(finalEnd))
                        .collect(Collectors.toList());
                List<PurchaseDto> list = purchases.stream().map(p -> new PurchaseDto(
                        p.getId(), p.getProduct().getId(), p.getProduct().getName(),
                        p.getQuantityPurchased(), p.getCostPrice(), p.getTotalAmount(),
                        p.getWorkingDate(), p.getCreatedAt()
                )).collect(Collectors.toList());
                response.setPurchaseReport(list);
                break;
            }
            case "EXPENSE": {
                List<Expense> expenses = expenseRepository.findByBusinessIdOrderByCreatedAtDesc(businessId).stream()
                        .filter(e -> !e.getWorkingDate().isBefore(finalStart) && !e.getWorkingDate().isAfter(finalEnd))
                        .collect(Collectors.toList());
                List<ExpenseDto> list = expenses.stream().map(e -> new ExpenseDto(
                        e.getId(), e.getCategory().getId(), e.getCategory().getName(),
                        e.getAmount(), e.getDescription(), e.getWorkingDate(), e.getCreatedAt()
                )).collect(Collectors.toList());
                response.setExpenseReport(list);
                break;
            }
            case "PROFIT": {
                List<Sale> sales = saleRepository.findByBusinessIdOrderByCreatedAtDesc(businessId).stream()
                        .filter(s -> !s.getWorkingDate().isBefore(finalStart) && !s.getWorkingDate().isAfter(finalEnd))
                        .collect(Collectors.toList());
                List<Expense> expenses = expenseRepository.findByBusinessIdOrderByCreatedAtDesc(businessId).stream()
                        .filter(e -> !e.getWorkingDate().isBefore(finalStart) && !e.getWorkingDate().isAfter(finalEnd))
                        .collect(Collectors.toList());

                BigDecimal revenue = sales.stream().map(Sale::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal productCost = sales.stream().map(s -> {
                    BigDecimal cp = s.getProduct().getCostPrice() != null ? s.getProduct().getCostPrice() : BigDecimal.ZERO;
                    return cp.multiply(BigDecimal.valueOf(s.getQuantitySold()));
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal grossProfit = revenue.subtract(productCost);
                BigDecimal totalExpenses = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal netProfit = grossProfit.subtract(totalExpenses);

                response.setProfitReport(new ProfitReportSummaryDto(revenue, productCost, grossProfit, totalExpenses, netProfit));
                break;
            }
            case "INVENTORY": {
                List<Product> products = productRepository.findByBusinessIdOrderByNameAsc(businessId);
                List<InventoryItemDto> list = products.stream().map(p -> {
                    int qty = p.getQuantity() != null ? p.getQuantity() : 0;
                    int min = p.getMinStockLevel() != null ? p.getMinStockLevel() : 0;
                    String status = qty == 0 ? "Out of Stock" : (qty <= min ? "Low Stock" : "In Stock");
                    String catName = p.getCategory() != null ? p.getCategory().getName() : "Uncategorized";
                    return new InventoryItemDto(p.getId(), p.getName(), catName, qty, p.getUnit(), min, status);
                }).collect(Collectors.toList());
                response.setInventoryReport(list);
                break;
            }
            case "SALES":
            default: {
                response.setReportType("SALES");
                List<Sale> sales = saleRepository.findByBusinessIdOrderByCreatedAtDesc(businessId).stream()
                        .filter(s -> !s.getWorkingDate().isBefore(finalStart) && !s.getWorkingDate().isAfter(finalEnd))
                        .collect(Collectors.toList());
                List<SaleDto> list = sales.stream().map(s -> new SaleDto(
                        s.getId(), s.getProduct().getId(), s.getProduct().getName(),
                        s.getQuantitySold(), s.getSellingPrice(), s.getTotalAmount(),
                        s.getWorkingDate(), s.getCreatedAt()
                )).collect(Collectors.toList());
                response.setSalesReport(list);
                break;
            }
        }

        return response;
    }
}

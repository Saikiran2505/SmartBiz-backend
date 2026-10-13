package com.smartbiz.service;

import com.smartbiz.dto.*;
import com.smartbiz.entity.*;
import com.smartbiz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final PurchaseRepository purchaseRepository;
    private final ExpenseRepository expenseRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;

    public DashboardService(BusinessRepository businessRepository,
                            ProductRepository productRepository,
                            SaleRepository saleRepository,
                            PurchaseRepository purchaseRepository,
                            ExpenseRepository expenseRepository,
                            StockAdjustmentRepository stockAdjustmentRepository) {
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.purchaseRepository = purchaseRepository;
        this.expenseRepository = expenseRepository;
        this.stockAdjustmentRepository = stockAdjustmentRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryDto getDashboardSummary(Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found."));

        LocalDate currentWorkingDate = business.getWorkingDate();
        int currentYear = currentWorkingDate.getYear();
        int currentMonth = currentWorkingDate.getMonthValue();

        List<Product> products = productRepository.findByBusinessIdOrderByNameAsc(businessId);
        List<Sale> sales = saleRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        List<Purchase> purchases = purchaseRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        List<Expense> expenses = expenseRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        List<StockAdjustment> adjustments = stockAdjustmentRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);

        DashboardSummaryDto summary = new DashboardSummaryDto();

        // 1. Today's Sales Calculation
        BigDecimal todaySales = sales.stream()
                .filter(s -> currentWorkingDate.equals(s.getWorkingDate()))
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTodaySales(todaySales);

        // 2. This Month's Sales Calculation
        BigDecimal monthSales = sales.stream()
                .filter(s -> s.getWorkingDate().getYear() == currentYear && s.getWorkingDate().getMonthValue() == currentMonth)
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setMonthSales(monthSales);

        // 3. This Month's Profit Calculation:
        // Product Cost (COGS) for items sold this month = sum(Cost Price * Quantity Sold)
        BigDecimal monthProductCost = sales.stream()
                .filter(s -> s.getWorkingDate().getYear() == currentYear && s.getWorkingDate().getMonthValue() == currentMonth)
                .map(s -> {
                    BigDecimal cost = (s.getProduct() != null && s.getProduct().getCostPrice() != null)
                            ? s.getProduct().getCostPrice()
                            : BigDecimal.ZERO;
                    return cost.multiply(BigDecimal.valueOf(s.getQuantitySold()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthExpenses = expenses.stream()
                .filter(e -> e.getWorkingDate().getYear() == currentYear && e.getWorkingDate().getMonthValue() == currentMonth)
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Gross Profit = Month Sales - Month Product Cost
        BigDecimal grossProfit = monthSales.subtract(monthProductCost);
        // Net Profit = Gross Profit - Month Expenses
        BigDecimal netProfit = grossProfit.subtract(monthExpenses);
        summary.setNetProfitMonth(netProfit);

        // 4. Low Stock Products & Alerts
        List<InventoryItemDto> lowStockAlerts = new ArrayList<>();
        for (Product product : products) {
            int qty = product.getQuantity() != null ? product.getQuantity() : 0;
            int minLevel = product.getMinStockLevel() != null ? product.getMinStockLevel() : 0;
            if (qty <= minLevel) {
                String status = qty == 0 ? "Out of Stock" : "Low Stock";
                String catName = product.getCategory() != null ? product.getCategory().getName() : "Uncategorized";
                lowStockAlerts.add(new InventoryItemDto(
                        product.getId(),
                        product.getName(),
                        catName,
                        qty,
                        product.getUnit() != null ? product.getUnit() : "pcs",
                        minLevel,
                        status
                ));
            }
        }
        summary.setLowStockCount(lowStockAlerts.size());
        summary.setLowStockAlerts(lowStockAlerts);

        // 5. Sales Trend (Group sales by workingDate for last 7 dates)
        Map<LocalDate, BigDecimal> salesByDateMap = new HashMap<>();
        for (Sale sale : sales) {
            salesByDateMap.merge(sale.getWorkingDate(), sale.getTotalAmount(), BigDecimal::add);
        }

        List<DailySalesDto> trendList = salesByDateMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .limit(7)
                .map(entry -> new DailySalesDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        summary.setSalesTrend(trendList);

        // 6. Top Selling Products
        Map<Long, TopProductDto> topProductMap = new HashMap<>();
        for (Sale sale : sales) {
            Long pId = sale.getProduct().getId();
            String pName = sale.getProduct().getName();
            TopProductDto dto = topProductMap.getOrDefault(pId, new TopProductDto(pId, pName, 0, BigDecimal.ZERO));
            dto.setTotalQuantitySold(dto.getTotalQuantitySold() + sale.getQuantitySold());
            dto.setTotalRevenue(dto.getTotalRevenue().add(sale.getTotalAmount()));
            topProductMap.put(pId, dto);
        }

        List<TopProductDto> topProductsList = topProductMap.values().stream()
                .sorted(Comparator.comparingInt(TopProductDto::getTotalQuantitySold).reversed())
                .limit(5)
                .collect(Collectors.toList());
        summary.setTopProducts(topProductsList);

        // 7. Recent Activity Feed (Sales, Purchases, Expenses, Adjustments)
        List<RecentActivityDto> activities = new ArrayList<>();

        for (Sale s : sales) {
            activities.add(new RecentActivityDto(
                    "SALE",
                    "Sale Recorded",
                    s.getQuantitySold() + " x " + s.getProduct().getName(),
                    s.getTotalAmount(),
                    s.getWorkingDate(),
                    s.getCreatedAt()
            ));
        }

        for (Purchase p : purchases) {
            activities.add(new RecentActivityDto(
                    "PURCHASE",
                    "Purchase Restock",
                    "+" + p.getQuantityPurchased() + " x " + p.getProduct().getName(),
                    p.getTotalAmount(),
                    p.getWorkingDate(),
                    p.getCreatedAt()
            ));
        }

        for (Expense e : expenses) {
            activities.add(new RecentActivityDto(
                    "EXPENSE",
                    "Expense: " + e.getCategory().getName(),
                    e.getDescription() != null && !e.getDescription().trim().isEmpty() ? e.getDescription() : e.getCategory().getName(),
                    e.getAmount(),
                    e.getWorkingDate(),
                    e.getCreatedAt()
            ));
        }

        for (StockAdjustment sa : adjustments) {
            String changeStr = (sa.getQuantityChange() > 0 ? "+" : "") + sa.getQuantityChange();
            activities.add(new RecentActivityDto(
                    "STOCK_ADJUSTMENT",
                    "Stock Adjustment (" + sa.getReason() + ")",
                    changeStr + " x " + sa.getProduct().getName(),
                    null,
                    sa.getWorkingDate(),
                    sa.getCreatedAt()
            ));
        }

        // Sort combined activity list descending by timestamp
        List<RecentActivityDto> sortedActivities = activities.stream()
                .sorted(Comparator.comparing(RecentActivityDto::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .collect(Collectors.toList());

        summary.setRecentActivities(sortedActivities);

        return summary;
    }
}

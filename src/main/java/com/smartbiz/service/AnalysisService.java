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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final PurchaseRepository purchaseRepository;
    private final ExpenseRepository expenseRepository;

    public AnalysisService(BusinessRepository businessRepository,
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
    public AnalysisSummaryDto getAnalysis(Long businessId, String filterType, LocalDate customStart, LocalDate customEnd) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found."));

        LocalDate workingDate = business.getWorkingDate();
        LocalDate startDate;
        LocalDate endDate;

        String type = (filterType != null && !filterType.trim().isEmpty()) ? filterType.trim().toUpperCase() : "THIS_MONTH";

        switch (type) {
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
                type = "THIS_MONTH";
                startDate = workingDate.withDayOfMonth(1);
                endDate = workingDate;
                break;
        }

        AnalysisSummaryDto dto = new AnalysisSummaryDto();
        dto.setFilterType(type);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);

        // Fetch all business records
        List<Sale> allSales = saleRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        List<Purchase> allPurchases = purchaseRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        List<Expense> allExpenses = expenseRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        List<Product> products = productRepository.findByBusinessIdOrderByNameAsc(businessId);

        final LocalDate finalStart = startDate;
        final LocalDate finalEnd = endDate;

        // Filter by Date Range [startDate, endDate]
        List<Sale> sales = allSales.stream()
                .filter(s -> !s.getWorkingDate().isBefore(finalStart) && !s.getWorkingDate().isAfter(finalEnd))
                .collect(Collectors.toList());

        List<Purchase> purchases = allPurchases.stream()
                .filter(p -> !p.getWorkingDate().isBefore(finalStart) && !p.getWorkingDate().isAfter(finalEnd))
                .collect(Collectors.toList());

        List<Expense> expenses = allExpenses.stream()
                .filter(e -> !e.getWorkingDate().isBefore(finalStart) && !e.getWorkingDate().isAfter(finalEnd))
                .collect(Collectors.toList());

        // 1. SALES ANALYSIS
        BigDecimal totalRevenue = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setSalesRevenue(totalRevenue);
        dto.setTotalRevenue(totalRevenue);
        dto.setSalesCount(sales.size());

        // Sales Trend
        Map<LocalDate, BigDecimal> salesTrendMap = new TreeMap<>();
        for (Sale s : sales) {
            salesTrendMap.merge(s.getWorkingDate(), s.getTotalAmount(), BigDecimal::add);
        }
        List<DateTrendDto> salesTrendList = salesTrendMap.entrySet().stream()
                .map(e -> new DateTrendDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        dto.setSalesTrend(salesTrendList);

        // 2. PRODUCT & PROFIT ANALYSIS
        BigDecimal totalProductCost = BigDecimal.ZERO;
        Map<Long, ProductProfitDto> productProfitMap = new HashMap<>();

        for (Sale s : sales) {
            Product p = s.getProduct();
            BigDecimal costPricePerUnit = p.getCostPrice() != null ? p.getCostPrice() : BigDecimal.ZERO;
            BigDecimal itemCost = costPricePerUnit.multiply(BigDecimal.valueOf(s.getQuantitySold()));
            BigDecimal itemProfit = s.getTotalAmount().subtract(itemCost);

            totalProductCost = totalProductCost.add(itemCost);

            ProductProfitDto pDto = productProfitMap.getOrDefault(p.getId(),
                    new ProductProfitDto(p.getId(), p.getName(), 0, BigDecimal.ZERO, BigDecimal.ZERO));
            pDto.setQuantitySold(pDto.getQuantitySold() + s.getQuantitySold());
            pDto.setRevenue(pDto.getRevenue().add(s.getTotalAmount()));
            pDto.setProfit(pDto.getProfit().add(itemProfit));
            productProfitMap.put(p.getId(), pDto);
        }

        dto.setTotalProductCost(totalProductCost);

        // Top products by Quantity
        List<ProductProfitDto> byQuantity = productProfitMap.values().stream()
                .sorted(Comparator.comparingInt(ProductProfitDto::getQuantitySold).reversed())
                .limit(10)
                .collect(Collectors.toList());
        dto.setTopProductsByQuantity(byQuantity);

        // Top products by Revenue
        List<ProductProfitDto> byRevenue = productProfitMap.values().stream()
                .sorted(Comparator.comparing(ProductProfitDto::getRevenue, Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toList());
        dto.setTopProductsByRevenue(byRevenue);

        // Top products by Profit
        List<ProductProfitDto> byProfit = productProfitMap.values().stream()
                .sorted(Comparator.comparing(ProductProfitDto::getProfit, Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toList());
        dto.setTopProductsByProfit(byProfit);

        // 3. CATEGORY ANALYSIS (Sales Revenue by Category)
        Map<String, CategorySummaryDto> catSalesMap = new HashMap<>();
        for (Sale s : sales) {
            String catName = s.getProduct().getCategory() != null ? s.getProduct().getCategory().getName() : "Uncategorized";
            CategorySummaryDto catDto = catSalesMap.getOrDefault(catName, new CategorySummaryDto(catName, BigDecimal.ZERO, 0L));
            catDto.setAmount(catDto.getAmount().add(s.getTotalAmount()));
            catDto.setItemCount(catDto.getItemCount() + 1);
            catSalesMap.put(catName, catDto);
        }
        List<CategorySummaryDto> revenueCatList = catSalesMap.values().stream()
                .sorted(Comparator.comparing(CategorySummaryDto::getAmount, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        dto.setRevenueByCategory(revenueCatList);

        // 4. PURCHASE ANALYSIS
        BigDecimal totalPurchase = purchases.stream()
                .map(Purchase::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalPurchaseAmount(totalPurchase);
        dto.setPurchaseCount(purchases.size());

        Map<LocalDate, BigDecimal> purTrendMap = new TreeMap<>();
        for (Purchase p : purchases) {
            purTrendMap.merge(p.getWorkingDate(), p.getTotalAmount(), BigDecimal::add);
        }
        List<DateTrendDto> purTrendList = purTrendMap.entrySet().stream()
                .map(e -> new DateTrendDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        dto.setPurchaseTrend(purTrendList);

        // 5. EXPENSE ANALYSIS
        BigDecimal totalExpense = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalExpenseAmount(totalExpense);
        dto.setTotalExpenses(totalExpense);
        dto.setExpenseCount(expenses.size());

        Map<LocalDate, BigDecimal> expTrendMap = new TreeMap<>();
        Map<String, CategorySummaryDto> expCatMap = new HashMap<>();

        for (Expense e : expenses) {
            expTrendMap.merge(e.getWorkingDate(), e.getAmount(), BigDecimal::add);
            String catName = e.getCategory().getName();
            CategorySummaryDto catDto = expCatMap.getOrDefault(catName, new CategorySummaryDto(catName, BigDecimal.ZERO, 0L));
            catDto.setAmount(catDto.getAmount().add(e.getAmount()));
            catDto.setItemCount(catDto.getItemCount() + 1);
            expCatMap.put(catName, catDto);
        }

        List<DateTrendDto> expTrendList = expTrendMap.entrySet().stream()
                .map(e -> new DateTrendDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        dto.setExpenseTrend(expTrendList);

        List<CategorySummaryDto> expCatList = expCatMap.values().stream()
                .sorted(Comparator.comparing(CategorySummaryDto::getAmount, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        dto.setExpensesByCategory(expCatList);

        // 6. PROFIT FORMULAS
        // Gross Profit = Sales Revenue - Product Cost
        BigDecimal grossProfit = totalRevenue.subtract(totalProductCost);
        // Net Profit = Gross Profit - Expenses
        BigDecimal netProfit = grossProfit.subtract(totalExpense);

        dto.setGrossProfit(grossProfit);
        dto.setNetProfit(netProfit);

        // 7. INVENTORY ANALYSIS (Current Snapshot)
        int totalCurrentStock = 0;
        int inStockCount = 0;
        List<InventoryItemDto> lowStockList = new ArrayList<>();
        List<InventoryItemDto> outOfStockList = new ArrayList<>();

        for (Product p : products) {
            int qty = p.getQuantity() != null ? p.getQuantity() : 0;
            int minLevel = p.getMinStockLevel() != null ? p.getMinStockLevel() : 0;
            totalCurrentStock += qty;

            String catName = p.getCategory() != null ? p.getCategory().getName() : "Uncategorized";

            if (qty == 0) {
                outOfStockList.add(new InventoryItemDto(p.getId(), p.getName(), catName, qty, p.getUnit(), minLevel, "Out of Stock"));
            } else if (qty <= minLevel) {
                lowStockList.add(new InventoryItemDto(p.getId(), p.getName(), catName, qty, p.getUnit(), minLevel, "Low Stock"));
            } else {
                inStockCount++;
            }
        }

        dto.setTotalCurrentStock(totalCurrentStock);
        dto.setInStockCount(inStockCount);
        dto.setLowStockCount(lowStockList.size());
        dto.setOutOfStockCount(outOfStockList.size());
        dto.setLowStockProducts(lowStockList);
        dto.setOutOfStockProducts(outOfStockList);

        return dto;
    }
}

package com.smartbiz.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.smartbiz.dto.*;
import com.smartbiz.entity.Business;
import com.smartbiz.repository.BusinessRepository;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class ReportExportService {

    private final ReportService reportService;
    private final BusinessRepository businessRepository;

    public ReportExportService(ReportService reportService, BusinessRepository businessRepository) {
        this.reportService = reportService;
        this.businessRepository = businessRepository;
    }

    /**
     * Generate CSV file content bytes (Excel compatible)
     */
    public byte[] generateCsv(Long businessId, String reportType, String filterType, String startDate, String endDate) {
        ReportResponseDto report = reportService.generateReport(
                businessId,
                reportType,
                filterType,
                startDate != null && !startDate.isEmpty() ? java.time.LocalDate.parse(startDate) : null,
                endDate != null && !endDate.isEmpty() ? java.time.LocalDate.parse(endDate) : null
        );

        Business business = businessRepository.findById(businessId).orElse(null);
        String businessName = business != null ? business.getName() : "SmartBiz";

        StringBuilder csv = new StringBuilder();

        // Header Metadata
        csv.append("Business,").append(escapeCsv(businessName)).append("\n");
        csv.append("Report Type,").append(report.getReportType()).append("\n");
        csv.append("Period,").append(report.getStartDate()).append(" to ").append(report.getEndDate()).append("\n\n");

        switch (report.getReportType()) {
            case "PURCHASE":
                csv.append("Date,Product,Quantity,Cost Price (INR),Total Amount (INR)\n");
                for (PurchaseDto p : report.getPurchaseReport()) {
                    csv.append(p.getWorkingDate()).append(",")
                       .append(escapeCsv(p.getProductName())).append(",")
                       .append(p.getQuantityPurchased()).append(",")
                       .append(p.getCostPrice()).append(",")
                       .append(p.getTotalAmount()).append("\n");
                }
                break;
            case "EXPENSE":
                csv.append("Date,Category,Amount (INR),Description\n");
                for (ExpenseDto e : report.getExpenseReport()) {
                    csv.append(e.getWorkingDate()).append(",")
                       .append(escapeCsv(e.getCategoryName())).append(",")
                       .append(e.getAmount()).append(",")
                       .append(escapeCsv(e.getDescription() != null ? e.getDescription() : "")).append("\n");
                }
                break;
            case "PROFIT":
                ProfitReportSummaryDto pr = report.getProfitReport();
                csv.append("Metric,Amount (INR)\n");
                csv.append("Sales Revenue,").append(pr != null ? pr.getRevenue() : "0.00").append("\n");
                csv.append("Product Cost,").append(pr != null ? pr.getProductCost() : "0.00").append("\n");
                csv.append("Gross Profit,").append(pr != null ? pr.getGrossProfit() : "0.00").append("\n");
                csv.append("Expenses,").append(pr != null ? pr.getExpenses() : "0.00").append("\n");
                csv.append("Net Profit,").append(pr != null ? pr.getNetProfit() : "0.00").append("\n");
                break;
            case "INVENTORY":
                csv.append("Product,Category,Current Stock,Minimum Stock,Status\n");
                for (InventoryItemDto inv : report.getInventoryReport()) {
                    csv.append(escapeCsv(inv.getProductName())).append(",")
                       .append(escapeCsv(inv.getCategoryName())).append(",")
                       .append(inv.getQuantity()).append(",")
                       .append(inv.getMinStockLevel()).append(",")
                       .append(inv.getStatus()).append("\n");
                }
                break;
            case "SALES":
            default:
                csv.append("Date,Product,Quantity,Selling Price (INR),Total Amount (INR)\n");
                for (SaleDto s : report.getSalesReport()) {
                    csv.append(s.getWorkingDate()).append(",")
                       .append(escapeCsv(s.getProductName())).append(",")
                       .append(s.getQuantitySold()).append(",")
                       .append(s.getSellingPrice()).append(",")
                       .append(s.getTotalAmount()).append("\n");
                }
                break;
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Generate PDF document byte stream using OpenPDF
     */
    public byte[] generatePdf(Long businessId, String reportType, String filterType, String startDate, String endDate) {
        ReportResponseDto report = reportService.generateReport(
                businessId,
                reportType,
                filterType,
                startDate != null && !startDate.isEmpty() ? java.time.LocalDate.parse(startDate) : null,
                endDate != null && !endDate.isEmpty() ? java.time.LocalDate.parse(endDate) : null
        );

        Business business = businessRepository.findById(businessId).orElse(null);
        String businessName = business != null ? business.getName() : "SmartBiz";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Document Header Styling
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(37, 99, 235));
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

            Paragraph title = new Paragraph("SmartBiz Business Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subTitle = new Paragraph(businessName + " - " + report.getReportType() + " REPORT", subTitleFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subTitle);

            Paragraph meta = new Paragraph("Period: " + report.getStartDate() + " to " + report.getEndDate() + " | Filter: " + report.getFilterType(), metaFont);
            meta.setAlignment(Element.ALIGN_CENTER);
            meta.setSpacingAfter(15);
            document.add(meta);

            Color headerBg = new Color(37, 99, 235);

            switch (report.getReportType()) {
                case "PURCHASE": {
                    PdfPTable table = new PdfPTable(5);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{2, 4, 2, 2.5f, 2.5f});
                    addHeaderCell(table, "Date", headerFont, headerBg);
                    addHeaderCell(table, "Product", headerFont, headerBg);
                    addHeaderCell(table, "Quantity", headerFont, headerBg);
                    addHeaderCell(table, "Cost Price", headerFont, headerBg);
                    addHeaderCell(table, "Total Amount", headerFont, headerBg);

                    for (PurchaseDto p : report.getPurchaseReport()) {
                        table.addCell(new Phrase(p.getWorkingDate().toString(), bodyFont));
                        table.addCell(new Phrase(p.getProductName(), bodyFont));
                        table.addCell(new Phrase("+" + p.getQuantityPurchased(), bodyFont));
                        table.addCell(new Phrase("INR " + p.getCostPrice(), bodyFont));
                        table.addCell(new Phrase("INR " + p.getTotalAmount(), bodyFont));
                    }
                    document.add(table);
                    break;
                }
                case "EXPENSE": {
                    PdfPTable table = new PdfPTable(4);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{2, 3, 2.5f, 4.5f});
                    addHeaderCell(table, "Date", headerFont, headerBg);
                    addHeaderCell(table, "Category", headerFont, headerBg);
                    addHeaderCell(table, "Amount", headerFont, headerBg);
                    addHeaderCell(table, "Description", headerFont, headerBg);

                    for (ExpenseDto e : report.getExpenseReport()) {
                        table.addCell(new Phrase(e.getWorkingDate().toString(), bodyFont));
                        table.addCell(new Phrase(e.getCategoryName(), bodyFont));
                        table.addCell(new Phrase("INR " + e.getAmount(), bodyFont));
                        table.addCell(new Phrase(e.getDescription() != null ? e.getDescription() : "-", bodyFont));
                    }
                    document.add(table);
                    break;
                }
                case "PROFIT": {
                    ProfitReportSummaryDto pr = report.getProfitReport();
                    PdfPTable table = new PdfPTable(2);
                    table.setWidthPercentage(70);
                    table.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.setWidths(new float[]{4, 3});

                    addHeaderCell(table, "Financial Metric", headerFont, headerBg);
                    addHeaderCell(table, "Amount (INR)", headerFont, headerBg);

                    table.addCell(new Phrase("Sales Revenue", bodyFont));
                    table.addCell(new Phrase("INR " + (pr != null ? pr.getRevenue() : "0.00"), bodyFont));

                    table.addCell(new Phrase("Product Cost", bodyFont));
                    table.addCell(new Phrase("INR " + (pr != null ? pr.getProductCost() : "0.00"), bodyFont));

                    table.addCell(new Phrase("Gross Profit", bodyFont));
                    table.addCell(new Phrase("INR " + (pr != null ? pr.getGrossProfit() : "0.00"), bodyFont));

                    table.addCell(new Phrase("Operating Expenses", bodyFont));
                    table.addCell(new Phrase("INR " + (pr != null ? pr.getExpenses() : "0.00"), bodyFont));

                    Font netFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
                    table.addCell(new Phrase("Net Profit", netFont));
                    table.addCell(new Phrase("INR " + (pr != null ? pr.getNetProfit() : "0.00"), netFont));

                    document.add(table);
                    break;
                }
                case "INVENTORY": {
                    PdfPTable table = new PdfPTable(5);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{4, 3, 2, 2, 2.5f});
                    addHeaderCell(table, "Product", headerFont, headerBg);
                    addHeaderCell(table, "Category", headerFont, headerBg);
                    addHeaderCell(table, "Current Stock", headerFont, headerBg);
                    addHeaderCell(table, "Min Stock", headerFont, headerBg);
                    addHeaderCell(table, "Status", headerFont, headerBg);

                    for (InventoryItemDto inv : report.getInventoryReport()) {
                        table.addCell(new Phrase(inv.getProductName(), bodyFont));
                        table.addCell(new Phrase(inv.getCategoryName(), bodyFont));
                        table.addCell(new Phrase(inv.getQuantity() + " " + inv.getUnit(), bodyFont));
                        table.addCell(new Phrase(inv.getMinStockLevel() + " " + inv.getUnit(), bodyFont));
                        table.addCell(new Phrase(inv.getStatus(), bodyFont));
                    }
                    document.add(table);
                    break;
                }
                case "SALES":
                default: {
                    PdfPTable table = new PdfPTable(5);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{2, 4, 2, 2.5f, 2.5f});
                    addHeaderCell(table, "Date", headerFont, headerBg);
                    addHeaderCell(table, "Product", headerFont, headerBg);
                    addHeaderCell(table, "Quantity", headerFont, headerBg);
                    addHeaderCell(table, "Selling Price", headerFont, headerBg);
                    addHeaderCell(table, "Total Amount", headerFont, headerBg);

                    for (SaleDto s : report.getSalesReport()) {
                        table.addCell(new Phrase(s.getWorkingDate().toString(), bodyFont));
                        table.addCell(new Phrase(s.getProductName(), bodyFont));
                        table.addCell(new Phrase(String.valueOf(s.getQuantitySold()), bodyFont));
                        table.addCell(new Phrase("INR " + s.getSellingPrice(), bodyFont));
                        table.addCell(new Phrase("INR " + s.getTotalAmount(), bodyFont));
                    }
                    document.add(table);
                    break;
                }
            }

            document.close();
        } catch (DocumentException ex) {
            throw new RuntimeException("Error creating PDF document", ex);
        }

        return out.toByteArray();
    }

    private void addHeaderCell(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
            return "\"" + input.replace("\"", "\"\"") + "\"";
        }
        return input;
    }
}

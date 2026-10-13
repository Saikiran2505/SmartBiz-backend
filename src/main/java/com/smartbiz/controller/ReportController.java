package com.smartbiz.controller;

import com.smartbiz.dto.ReportResponseDto;
import com.smartbiz.security.CustomUserDetails;
import com.smartbiz.service.ReportExportService;
import com.smartbiz.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final ReportExportService reportExportService;

    public ReportController(ReportService reportService, ReportExportService reportExportService) {
        this.reportService = reportService;
        this.reportExportService = reportExportService;
    }

    @GetMapping
    public ResponseEntity<ReportResponseDto> getReport(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false, defaultValue = "SALES") String reportType,
            @RequestParam(required = false, defaultValue = "THIS_MONTH") String filterType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        ReportResponseDto dto = reportService.generateReport(
                userDetails.getBusinessId(),
                reportType,
                filterType,
                startDate,
                endDate
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/download/excel")
    public ResponseEntity<byte[]> downloadExcel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false, defaultValue = "SALES") String reportType,
            @RequestParam(required = false, defaultValue = "THIS_MONTH") String filterType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        byte[] csvData = reportExportService.generateCsv(
                userDetails.getBusinessId(),
                reportType,
                filterType,
                startDate,
                endDate
        );

        String filename = reportType.toLowerCase() + "_report.csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/download/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false, defaultValue = "SALES") String reportType,
            @RequestParam(required = false, defaultValue = "THIS_MONTH") String filterType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        byte[] pdfData = reportExportService.generatePdf(
                userDetails.getBusinessId(),
                reportType,
                filterType,
                startDate,
                endDate
        );

        String filename = reportType.toLowerCase() + "_report.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }
}

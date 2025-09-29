package com.ayd.sie.reports.infrastructure.web;

import com.ayd.sie.reports.application.dto.*;
import com.ayd.sie.reports.application.services.ReportGeneratorService;
import com.ayd.sie.reports.infrastructure.export.PdfExporter;
import com.ayd.sie.reports.infrastructure.export.ExcelExporter;
import com.ayd.sie.reports.infrastructure.export.ImageExporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reports", description = "Report generation endpoints for administrators and coordinators")
public class ReportsController {

    private final ReportGeneratorService reportGeneratorService;
    private final PdfExporter pdfExporter;
    private final ExcelExporter excelExporter;
    private final ImageExporter imageExporter;
    private final ObjectMapper objectMapper;

    @GetMapping("/deliveries")
    @Operation(summary = "Generate delivery status report", description = "Generate report showing completed, cancelled and rejected deliveries for a specific period")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery report generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date parameters"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DeliveryReportDto> generateDeliveryReport(
            @Parameter(description = "Start date for the report", example = "2025-01-01", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for the report", example = "2025-01-31", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Generating delivery report for period: {} to {}", startDate, endDate);

        try {
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().build();
            }

            DeliveryReportDto report = reportGeneratorService.generateDeliveryReport(startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Error generating delivery report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/commissions")
    @Operation(summary = "Generate commission report by courier", description = "Generate report showing commissions earned by each courier for a specific period")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Commission report generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date parameters"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CommissionReportDto>> generateCommissionReport(
            @Parameter(description = "Start date for the report", example = "2025-01-01", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for the report", example = "2025-01-31", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Generating commission report for period: {} to {}", startDate, endDate);

        try {
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().build();
            }

            List<CommissionReportDto> reports = reportGeneratorService.generateCommissionReport(startDate, endDate);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            log.error("Error generating commission report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/discounts")
    @Operation(summary = "Generate discount report by loyalty level", description = "Generate report showing discounts applied by loyalty level for businesses")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Discount report generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date parameters"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DiscountReportDto>> generateDiscountReport(
            @Parameter(description = "Start date for the report", example = "2025-01-01", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for the report", example = "2025-01-31", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Generating discount report for period: {} to {}", startDate, endDate);

        try {
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().build();
            }

            List<DiscountReportDto> reports = reportGeneratorService.generateDiscountReport(startDate, endDate);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            log.error("Error generating discount report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/business-ranking")
    @Operation(summary = "Generate business ranking report", description = "Generate ranking report of businesses by monthly volume")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Business ranking report generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date parameters"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<RankingReportDto>> generateBusinessRankingReport(
            @Parameter(description = "Start date for the report", example = "2025-01-01", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for the report", example = "2025-01-31", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Generating business ranking report for period: {} to {}", startDate, endDate);

        try {
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().build();
            }

            List<RankingReportDto> reports = reportGeneratorService.generateBusinessRankingReport(startDate, endDate);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            log.error("Error generating business ranking report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/cancellations")
    @Operation(summary = "Generate cancellations by business report", description = "Generate report showing cancellations by business and category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cancellations report generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date parameters"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<RankingReportDto>> generateCancellationsReport(
            @Parameter(description = "Start date for the report", example = "2025-01-01", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for the report", example = "2025-01-31", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Generating cancellations by business report for period: {} to {}", startDate, endDate);

        try {
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().build();
            }

            List<RankingReportDto> reports = reportGeneratorService.generateCancellationsByBusinessReport(startDate,
                    endDate);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            log.error("Error generating cancellations report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Export endpoints
    @GetMapping("/deliveries/export/pdf")
    @Operation(summary = "Export delivery report to PDF", description = "Export delivery status report to PDF format")
    public ResponseEntity<byte[]> exportDeliveryReportToPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            DeliveryReportDto report = reportGeneratorService.generateDeliveryReport(startDate, endDate);
            String content = objectMapper.writeValueAsString(report);
            byte[] pdfData = pdfExporter.exportToPdf("Delivery Report", content);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "delivery-report.pdf");

            return ResponseEntity.ok().headers(headers).body(pdfData);
        } catch (Exception e) {
            log.error("Error exporting delivery report to PDF: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/commissions/export/excel")
    @Operation(summary = "Export commission report to Excel", description = "Export commission report to Excel format")
    public ResponseEntity<byte[]> exportCommissionReportToExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<CommissionReportDto> reports = reportGeneratorService.generateCommissionReport(startDate, endDate);

            List<String> headers = List.of("courier_name", "courier_email", "total_deliveries",
                    "completed_deliveries", "total_commission", "completion_rate");

            List<Map<String, Object>> data = reports.stream()
                    .map(report -> {
                        Map<String, Object> row = new HashMap<>();
                        row.put("courier_name", report.getCourierName());
                        row.put("courier_email", report.getCourierEmail());
                        row.put("total_deliveries", report.getTotalDeliveries());
                        row.put("completed_deliveries", report.getCompletedDeliveries());
                        row.put("total_commission", report.getTotalCommission());
                        row.put("completion_rate", report.getCompletionRate());
                        return row;
                    })
                    .toList();

            byte[] excelData = excelExporter.exportToExcel("Commission Report", headers, data);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentDispositionFormData("attachment", "commission-report.xlsx");

            return ResponseEntity.ok().headers(httpHeaders).body(excelData);
        } catch (Exception e) {
            log.error("Error exporting commission report to Excel: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/business-ranking/export/image")
    @Operation(summary = "Export business ranking report to image", description = "Export business ranking report to image format")
    public ResponseEntity<byte[]> exportBusinessRankingReportToImage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "png") String format) {

        try {
            List<RankingReportDto> reports = reportGeneratorService.generateBusinessRankingReport(startDate, endDate);
            String content = objectMapper.writeValueAsString(reports);
            byte[] imageData = imageExporter.exportToImage("Business Ranking Report", content, format);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "business-ranking." + format);

            return ResponseEntity.ok().headers(headers).body(imageData);
        } catch (Exception e) {
            log.error("Error exporting business ranking report to image: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
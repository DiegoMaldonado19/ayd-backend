package com.ayd.sie.reports.infrastructure.web;

import com.ayd.sie.reports.application.dto.*;
import com.ayd.sie.reports.application.services.ReportGeneratorService;
import com.ayd.sie.reports.application.services.ReportExportService;
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
    private final ReportExportService reportExportService;
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

    @GetMapping("/deliveries/export/excel")
    @Operation(summary = "Export delivery report to Excel", description = "Export delivery status report to Excel format")
    public ResponseEntity<byte[]> exportDeliveryReportToExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            DeliveryReportDto report = reportGeneratorService.generateDeliveryReport(startDate, endDate);

            List<String> headers = List.of("report_date", "period_start", "period_end", "completed_deliveries",
                    "cancelled_deliveries", "rejected_deliveries", "total_deliveries", "completion_rate");

            List<Map<String, Object>> data = List.of(Map.of(
                    "report_date", report.getReportDate(),
                    "period_start", report.getPeriodStart(),
                    "period_end", report.getPeriodEnd(),
                    "completed_deliveries", report.getCompletedDeliveries(),
                    "cancelled_deliveries", report.getCancelledDeliveries(),
                    "rejected_deliveries", report.getRejectedDeliveries(),
                    "total_deliveries", report.getTotalDeliveries(),
                    "completion_rate", report.getCompletionRate()));

            byte[] excelData = excelExporter.exportToExcel("Delivery Report", headers, data);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentDispositionFormData("attachment", "delivery-report.xlsx");

            return ResponseEntity.ok().headers(httpHeaders).body(excelData);
        } catch (Exception e) {
            log.error("Error exporting delivery report to Excel: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/deliveries/export/image")
    @Operation(summary = "Export delivery report to image", description = "Export delivery status report to image format")
    public ResponseEntity<byte[]> exportDeliveryReportToImage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "png") String format) {

        try {
            DeliveryReportDto report = reportGeneratorService.generateDeliveryReport(startDate, endDate);
            String content = objectMapper.writeValueAsString(report);
            byte[] imageData = imageExporter.exportToImage("Delivery Report", content, format);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "delivery-report." + format);

            return ResponseEntity.ok().headers(headers).body(imageData);
        } catch (Exception e) {
            log.error("Error exporting delivery report to image: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/commissions/export/pdf")
    @Operation(summary = "Export commission report to PDF", description = "Export commission report to PDF format")
    public ResponseEntity<byte[]> exportCommissionReportToPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<CommissionReportDto> reports = reportGeneratorService.generateCommissionReport(startDate, endDate);
            byte[] pdfData = reportExportService.exportToPdf("Commission Report", reports);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(reportExportService.getContentType("pdf")));
            headers.setContentDispositionFormData("attachment",
                    reportExportService.getExportFilename("commission-report", "pdf"));

            return ResponseEntity.ok().headers(headers).body(pdfData);
        } catch (Exception e) {
            log.error("Error exporting commission report to PDF: {}", e.getMessage(), e);
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

    @GetMapping("/commissions/export/image")
    @Operation(summary = "Export commission report to image", description = "Export commission report to image format")
    public ResponseEntity<byte[]> exportCommissionReportToImage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "png") String format) {

        try {
            List<CommissionReportDto> reports = reportGeneratorService.generateCommissionReport(startDate, endDate);
            byte[] imageData = reportExportService.exportToImage("Commission Report", reports, format);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(reportExportService.getContentType(format)));
            headers.setContentDispositionFormData("attachment",
                    reportExportService.getExportFilename("commission-report", format));

            return ResponseEntity.ok().headers(headers).body(imageData);
        } catch (Exception e) {
            log.error("Error exporting commission report to image: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/business-ranking/export/pdf")
    @Operation(summary = "Export business ranking report to PDF", description = "Export business ranking report to PDF format")
    public ResponseEntity<byte[]> exportBusinessRankingReportToPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<RankingReportDto> reports = reportGeneratorService.generateBusinessRankingReport(startDate, endDate);
            byte[] pdfData = reportExportService.exportToPdf("Business Ranking Report", reports);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(reportExportService.getContentType("pdf")));
            headers.setContentDispositionFormData("attachment",
                    reportExportService.getExportFilename("business-ranking-report", "pdf"));

            return ResponseEntity.ok().headers(headers).body(pdfData);
        } catch (Exception e) {
            log.error("Error exporting business ranking report to PDF: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/business-ranking/export/excel")
    @Operation(summary = "Export business ranking report to Excel", description = "Export business ranking report to Excel format")
    public ResponseEntity<byte[]> exportBusinessRankingReportToExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<RankingReportDto> reports = reportGeneratorService.generateBusinessRankingReport(startDate, endDate);

            List<String> headers = List.of("rank_position", "business_name", "business_email", "loyalty_level",
                    "total_deliveries", "completed_deliveries", "cancelled_deliveries", "total_revenue",
                    "completion_rate", "average_delivery_value");

            List<Map<String, Object>> data = reports.stream()
                    .map(report -> {
                        Map<String, Object> row = new HashMap<>();
                        row.put("rank_position", report.getRankPosition());
                        row.put("business_name", report.getBusinessName());
                        row.put("business_email", report.getBusinessEmail());
                        row.put("loyalty_level", report.getLoyaltyLevel());
                        row.put("total_deliveries", report.getTotalDeliveries());
                        row.put("completed_deliveries", report.getCompletedDeliveries());
                        row.put("cancelled_deliveries", report.getCancelledDeliveries());
                        row.put("total_revenue", report.getTotalRevenue());
                        row.put("completion_rate", report.getCompletionRate());
                        row.put("average_delivery_value", report.getAverageDeliveryValue());
                        return row;
                    })
                    .toList();

            byte[] excelData = reportExportService.exportToExcel("Business Ranking Report", headers, data);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.valueOf(reportExportService.getContentType("excel")));
            httpHeaders.setContentDispositionFormData("attachment",
                    reportExportService.getExportFilename("business-ranking-report", "excel"));

            return ResponseEntity.ok().headers(httpHeaders).body(excelData);
        } catch (Exception e) {
            log.error("Error exporting business ranking report to Excel: {}", e.getMessage(), e);
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

    // Discount Report Export Endpoints
    @GetMapping("/discounts/export/pdf")
    @Operation(summary = "Export discount report to PDF", description = "Export discount report to PDF format")
    public ResponseEntity<byte[]> exportDiscountReportToPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<DiscountReportDto> reports = reportGeneratorService.generateDiscountReport(startDate, endDate);
            byte[] pdfData = reportExportService.exportToPdf("Discount Report", reports);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(reportExportService.getContentType("pdf")));
            headers.setContentDispositionFormData("attachment",
                    reportExportService.getExportFilename("discount-report", "pdf"));

            return ResponseEntity.ok().headers(headers).body(pdfData);
        } catch (Exception e) {
            log.error("Error exporting discount report to PDF: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/discounts/export/excel")
    @Operation(summary = "Export discount report to Excel", description = "Export discount report to Excel format")
    public ResponseEntity<byte[]> exportDiscountReportToExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<DiscountReportDto> reports = reportGeneratorService.generateDiscountReport(startDate, endDate);

            List<String> headers = List.of("business_name", "loyalty_level", "period_start", "period_end",
                    "total_deliveries", "completed_deliveries", "cancelled_deliveries", "total_amount",
                    "discount_percentage", "discount_amount", "final_amount");

            List<Map<String, Object>> data = reports.stream()
                    .map(report -> {
                        Map<String, Object> row = new HashMap<>();
                        row.put("business_name", report.getBusinessName());
                        row.put("loyalty_level", report.getLoyaltyLevel());
                        row.put("period_start", report.getPeriodStart());
                        row.put("period_end", report.getPeriodEnd());
                        row.put("total_deliveries", report.getTotalDeliveries());
                        row.put("completed_deliveries", report.getCompletedDeliveries());
                        row.put("cancelled_deliveries", report.getCancelledDeliveries());
                        row.put("total_amount", report.getTotalAmount());
                        row.put("discount_percentage", report.getDiscountPercentage());
                        row.put("discount_amount", report.getDiscountAmount());
                        row.put("final_amount", report.getFinalAmount());
                        return row;
                    })
                    .toList();

            byte[] excelData = reportExportService.exportToExcel("Discount Report", headers, data);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.valueOf(reportExportService.getContentType("excel")));
            httpHeaders.setContentDispositionFormData("attachment",
                    reportExportService.getExportFilename("discount-report", "excel"));

            return ResponseEntity.ok().headers(httpHeaders).body(excelData);
        } catch (Exception e) {
            log.error("Error exporting discount report to Excel: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/discounts/export/image")
    @Operation(summary = "Export discount report to image", description = "Export discount report to image format")
    public ResponseEntity<byte[]> exportDiscountReportToImage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "png") String format) {

        try {
            List<DiscountReportDto> reports = reportGeneratorService.generateDiscountReport(startDate, endDate);
            byte[] imageData = reportExportService.exportToImage("Discount Report", reports, format);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(reportExportService.getContentType(format)));
            headers.setContentDispositionFormData("attachment",
                    reportExportService.getExportFilename("discount-report", format));

            return ResponseEntity.ok().headers(headers).body(imageData);
        } catch (Exception e) {
            log.error("Error exporting discount report to image: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Cancellations Report Export Endpoints
    @GetMapping("/cancellations/export/pdf")
    @Operation(summary = "Export cancellations report to PDF", description = "Export cancellations report to PDF format")
    public ResponseEntity<byte[]> exportCancellationsReportToPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<RankingReportDto> reports = reportGeneratorService.generateCancellationsByBusinessReport(startDate,
                    endDate);
            byte[] pdfData = reportExportService.exportToPdf("Cancellations Report", reports);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(reportExportService.getContentType("pdf")));
            headers.setContentDispositionFormData("attachment",
                    reportExportService.getExportFilename("cancellations-report", "pdf"));

            return ResponseEntity.ok().headers(headers).body(pdfData);
        } catch (Exception e) {
            log.error("Error exporting cancellations report to PDF: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/cancellations/export/excel")
    @Operation(summary = "Export cancellations report to Excel", description = "Export cancellations report to Excel format")
    public ResponseEntity<byte[]> exportCancellationsReportToExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<RankingReportDto> reports = reportGeneratorService.generateCancellationsByBusinessReport(startDate,
                    endDate);

            List<String> headers = List.of("rank_position", "business_name", "business_email", "loyalty_level",
                    "total_deliveries", "completed_deliveries", "cancelled_deliveries", "total_revenue",
                    "completion_rate", "average_delivery_value");

            List<Map<String, Object>> data = reports.stream()
                    .map(report -> {
                        Map<String, Object> row = new HashMap<>();
                        row.put("rank_position", report.getRankPosition());
                        row.put("business_name", report.getBusinessName());
                        row.put("business_email", report.getBusinessEmail());
                        row.put("loyalty_level", report.getLoyaltyLevel());
                        row.put("total_deliveries", report.getTotalDeliveries());
                        row.put("completed_deliveries", report.getCompletedDeliveries());
                        row.put("cancelled_deliveries", report.getCancelledDeliveries());
                        row.put("total_revenue", report.getTotalRevenue());
                        row.put("completion_rate", report.getCompletionRate());
                        row.put("average_delivery_value", report.getAverageDeliveryValue());
                        return row;
                    })
                    .toList();

            byte[] excelData = reportExportService.exportToExcel("Cancellations Report", headers, data);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.valueOf(reportExportService.getContentType("excel")));
            httpHeaders.setContentDispositionFormData("attachment",
                    reportExportService.getExportFilename("cancellations-report", "excel"));

            return ResponseEntity.ok().headers(httpHeaders).body(excelData);
        } catch (Exception e) {
            log.error("Error exporting cancellations report to Excel: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/cancellations/export/image")
    @Operation(summary = "Export cancellations report to image", description = "Export cancellations report to image format")
    public ResponseEntity<byte[]> exportCancellationsReportToImage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "png") String format) {

        try {
            List<RankingReportDto> reports = reportGeneratorService.generateCancellationsByBusinessReport(startDate,
                    endDate);
            byte[] imageData = reportExportService.exportToImage("Cancellations Report", reports, format);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(reportExportService.getContentType(format)));
            headers.setContentDispositionFormData("attachment",
                    reportExportService.getExportFilename("cancellations-report", format));

            return ResponseEntity.ok().headers(headers).body(imageData);
        } catch (Exception e) {
            log.error("Error exporting cancellations report to image: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
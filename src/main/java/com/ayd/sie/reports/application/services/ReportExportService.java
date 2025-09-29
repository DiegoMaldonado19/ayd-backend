package com.ayd.sie.reports.application.services;

import com.ayd.sie.reports.infrastructure.export.ExcelExporter;
import com.ayd.sie.reports.infrastructure.export.ImageExporter;
import com.ayd.sie.reports.infrastructure.export.PdfExporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Generic export service for all report types.
 * Provides unified export functionality for PDF, Excel, and Image formats.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportExportService {

    private final PdfExporter pdfExporter;
    private final ExcelExporter excelExporter;
    private final ImageExporter imageExporter;
    private final ObjectMapper objectMapper;

    /**
     * Export report data to PDF format.
     *
     * @param title Report title
     * @param data  Report data object
     * @return PDF as byte array
     * @throws IOException if export fails
     */
    public byte[] exportToPdf(String title, Object data) throws IOException {
        log.info("Exporting report '{}' to PDF", title);
        try {
            String content = objectMapper.writeValueAsString(data);
            return pdfExporter.exportToPdf(title, content);
        } catch (Exception e) {
            log.error("Error exporting '{}' to PDF: {}", title, e.getMessage(), e);
            throw new IOException("Failed to export report to PDF", e);
        }
    }

    /**
     * Export report data to Excel format.
     *
     * @param sheetName Excel sheet name
     * @param headers   Column headers
     * @param data      Report data as list of maps
     * @return Excel file as byte array
     * @throws IOException if export fails
     */
    public byte[] exportToExcel(String sheetName, List<String> headers, List<Map<String, Object>> data)
            throws IOException {
        log.info("Exporting report '{}' to Excel", sheetName);
        try {
            return excelExporter.exportToExcel(sheetName, headers, data);
        } catch (Exception e) {
            log.error("Error exporting '{}' to Excel: {}", sheetName, e.getMessage(), e);
            throw new IOException("Failed to export report to Excel", e);
        }
    }

    /**
     * Export report data to Image format.
     *
     * @param title  Report title
     * @param data   Report data object
     * @param format Image format (png, jpg, etc.)
     * @return Image as byte array
     * @throws IOException if export fails
     */
    public byte[] exportToImage(String title, Object data, String format) throws IOException {
        log.info("Exporting report '{}' to Image ({})", title, format);
        try {
            String content = objectMapper.writeValueAsString(data);
            return imageExporter.exportToImage(title, content, format);
        } catch (Exception e) {
            log.error("Error exporting '{}' to Image: {}", title, e.getMessage(), e);
            throw new IOException("Failed to export report to Image", e);
        }
    }

    /**
     * Get appropriate filename for export based on format.
     *
     * @param reportName Report name
     * @param format     Export format
     * @return Filename with proper extension
     */
    public String getExportFilename(String reportName, String format) {
        String sanitizedName = reportName.toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-zA-Z0-9-]", "");

        return switch (format.toLowerCase()) {
            case "pdf" -> sanitizedName + ".pdf";
            case "excel", "xlsx" -> sanitizedName + ".xlsx";
            case "png" -> sanitizedName + ".png";
            case "jpg", "jpeg" -> sanitizedName + ".jpg";
            default -> sanitizedName + "." + format;
        };
    }

    /**
     * Get appropriate content type for export format.
     *
     * @param format Export format
     * @return MIME type string
     */
    public String getContentType(String format) {
        return switch (format.toLowerCase()) {
            case "pdf" -> "application/pdf";
            case "excel", "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            default -> "application/octet-stream";
        };
    }
}
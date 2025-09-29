package com.ayd.sie.reports.infrastructure.export;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExcelExporter {

    public byte[] exportToExcel(String sheetName, List<String> headers, List<Map<String, Object>> data)
            throws IOException {
        log.info("Exporting report to Excel: {}", sheetName);

        try {
            // For this implementation, we'll create a simple CSV-like content
            // In a real application, you would use Apache POI library
            String excelContent = createSimpleExcelContent(sheetName, headers, data);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(excelContent.getBytes());

            log.info("Excel export completed successfully");
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error exporting to Excel: {}", e.getMessage(), e);
            throw new IOException("Failed to export to Excel", e);
        }
    }

    private String createSimpleExcelContent(String sheetName, List<String> headers, List<Map<String, Object>> data) {
        StringBuilder content = new StringBuilder();

        // Add metadata
        content.append("Sheet: ").append(sheetName).append("\\n");
        content.append("Generated: ").append(LocalDateTime.now()).append("\\n");
        content.append("\\n");

        // Add headers
        content.append(String.join(",", headers)).append("\\n");

        // Add data rows
        for (Map<String, Object> row : data) {
            List<String> values = headers.stream()
                    .map(header -> {
                        Object value = row.get(header);
                        return value != null ? value.toString() : "";
                    })
                    .toList();
            content.append(String.join(",", values)).append("\\n");
        }

        return content.toString();
    }
}
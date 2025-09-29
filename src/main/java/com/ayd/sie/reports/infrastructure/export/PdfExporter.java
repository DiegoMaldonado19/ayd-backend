package com.ayd.sie.reports.infrastructure.export;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class PdfExporter {

    public byte[] exportToPdf(String title, String content) throws IOException {
        log.info("Exporting report to PDF: {}", title);

        try {
            // For this implementation, we'll create a simple PDF content
            // In a real application, you would use a library like iText or Flying Saucer
            String pdfContent = createSimplePdfContent(title, content);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(pdfContent.getBytes());

            log.info("PDF export completed successfully");
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error exporting to PDF: {}", e.getMessage(), e);
            throw new IOException("Failed to export to PDF", e);
        }
    }

    private String createSimplePdfContent(String title, String content) {
        // This is a simplified PDF content representation
        // In a real implementation, you would generate proper PDF format
        return String.format("""
                %PDF-1.4
                %%PDF Document

                Title: %s
                Generated: %s

                Content:
                %s

                End of Document
                """, title, java.time.LocalDateTime.now(), content);
    }
}
package com.ayd.sie.reports.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Delivery status report showing completed, cancelled and rejected deliveries")
public class DeliveryReportDto {

    @JsonProperty("report_date")
    @Schema(description = "Date of the report", example = "2025-01-15")
    private LocalDate reportDate;

    @JsonProperty("period_start")
    @Schema(description = "Start date of the reporting period", example = "2025-01-01")
    private LocalDate periodStart;

    @JsonProperty("period_end")
    @Schema(description = "End date of the reporting period", example = "2025-01-31")
    private LocalDate periodEnd;

    @JsonProperty("completed_deliveries")
    @Schema(description = "Number of completed deliveries", example = "150")
    private Long completedDeliveries;

    @JsonProperty("cancelled_deliveries")
    @Schema(description = "Number of cancelled deliveries", example = "12")
    private Long cancelledDeliveries;

    @JsonProperty("rejected_deliveries")
    @Schema(description = "Number of rejected deliveries", example = "8")
    private Long rejectedDeliveries;

    @JsonProperty("total_deliveries")
    @Schema(description = "Total number of deliveries", example = "170")
    private Long totalDeliveries;

    @JsonProperty("completion_rate")
    @Schema(description = "Percentage of completed deliveries", example = "88.24")
    private Double completionRate;

    @JsonProperty("cancellation_rate")
    @Schema(description = "Percentage of cancelled deliveries", example = "7.06")
    private Double cancellationRate;

    @JsonProperty("rejection_rate")
    @Schema(description = "Percentage of rejected deliveries", example = "4.71")
    private Double rejectionRate;

    // Calculate rates
    public void calculateRates() {
        if (totalDeliveries > 0) {
            this.completionRate = (completedDeliveries * 100.0) / totalDeliveries;
            this.cancellationRate = (cancelledDeliveries * 100.0) / totalDeliveries;
            this.rejectionRate = (rejectedDeliveries * 100.0) / totalDeliveries;
        } else {
            this.completionRate = 0.0;
            this.cancellationRate = 0.0;
            this.rejectionRate = 0.0;
        }
    }
}
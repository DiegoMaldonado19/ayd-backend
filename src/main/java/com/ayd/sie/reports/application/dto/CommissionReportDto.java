package com.ayd.sie.reports.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Commission report for couriers by period")
public class CommissionReportDto {

    @JsonProperty("courier_id")
    @Schema(description = "Courier unique identifier", example = "123")
    private Integer courierId;

    @JsonProperty("courier_name")
    @Schema(description = "Full name of the courier", example = "Pedro González")
    private String courierName;

    @JsonProperty("courier_email")
    @Schema(description = "Email of the courier", example = "pedro.gonzalez@sie.com.gt")
    private String courierEmail;

    @JsonProperty("period_start")
    @Schema(description = "Start date of the reporting period", example = "2025-01-01")
    private LocalDate periodStart;

    @JsonProperty("period_end")
    @Schema(description = "End date of the reporting period", example = "2025-01-31")
    private LocalDate periodEnd;

    @JsonProperty("total_deliveries")
    @Schema(description = "Total number of deliveries completed", example = "45")
    private Long totalDeliveries;

    @JsonProperty("completed_deliveries")
    @Schema(description = "Number of successfully completed deliveries", example = "42")
    private Long completedDeliveries;

    @JsonProperty("cancelled_deliveries")
    @Schema(description = "Number of cancelled deliveries", example = "3")
    private Long cancelledDeliveries;

    @JsonProperty("total_commission")
    @Schema(description = "Total commission earned", example = "1250.75")
    private BigDecimal totalCommission;

    @JsonProperty("average_commission_per_delivery")
    @Schema(description = "Average commission per completed delivery", example = "29.78")
    private BigDecimal averageCommissionPerDelivery;

    @JsonProperty("completion_rate")
    @Schema(description = "Percentage of completed deliveries", example = "93.33")
    private Double completionRate;

    // Calculate derived fields
    public void calculateDerivedFields() {
        if (totalDeliveries > 0) {
            this.completionRate = (completedDeliveries * 100.0) / totalDeliveries;
        } else {
            this.completionRate = 0.0;
        }

        if (completedDeliveries > 0 && totalCommission != null) {
            this.averageCommissionPerDelivery = totalCommission.divide(
                    BigDecimal.valueOf(completedDeliveries), 2, RoundingMode.HALF_UP);
        } else {
            this.averageCommissionPerDelivery = BigDecimal.ZERO;
        }
    }
}
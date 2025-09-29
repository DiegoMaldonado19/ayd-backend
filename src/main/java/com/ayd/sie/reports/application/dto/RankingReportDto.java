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
@Schema(description = "Business ranking report by monthly volume")
public class RankingReportDto {

    @JsonProperty("rank_position")
    @Schema(description = "Position in the ranking", example = "1")
    private Integer rankPosition;

    @JsonProperty("business_id")
    @Schema(description = "Business unique identifier", example = "456")
    private Integer businessId;

    @JsonProperty("business_name")
    @Schema(description = "Name of the business", example = "Tienda Electrónica")
    private String businessName;

    @JsonProperty("business_email")
    @Schema(description = "Business contact email", example = "tienda.electronica@gmail.com")
    private String businessEmail;

    @JsonProperty("loyalty_level")
    @Schema(description = "Current loyalty level", example = "Diamante")
    private String loyaltyLevel;

    @JsonProperty("period_start")
    @Schema(description = "Start date of the reporting period", example = "2025-01-01")
    private LocalDate periodStart;

    @JsonProperty("period_end")
    @Schema(description = "End date of the reporting period", example = "2025-01-31")
    private LocalDate periodEnd;

    @JsonProperty("total_deliveries")
    @Schema(description = "Total number of deliveries", example = "125")
    private Long totalDeliveries;

    @JsonProperty("completed_deliveries")
    @Schema(description = "Number of completed deliveries", example = "118")
    private Long completedDeliveries;

    @JsonProperty("cancelled_deliveries")
    @Schema(description = "Number of cancelled deliveries", example = "7")
    private Long cancelledDeliveries;

    @JsonProperty("total_revenue")
    @Schema(description = "Total revenue generated", example = "15750.50")
    private BigDecimal totalRevenue;

    @JsonProperty("completion_rate")
    @Schema(description = "Percentage of completed deliveries", example = "94.40")
    private Double completionRate;

    @JsonProperty("average_delivery_value")
    @Schema(description = "Average value per delivery", example = "126.00")
    private BigDecimal averageDeliveryValue;

    // Calculate derived fields
    public void calculateDerivedFields() {
        if (totalDeliveries > 0) {
            this.completionRate = (completedDeliveries * 100.0) / totalDeliveries;
            if (totalRevenue != null) {
                this.averageDeliveryValue = totalRevenue.divide(
                        BigDecimal.valueOf(totalDeliveries), 2, RoundingMode.HALF_UP);
            }
        } else {
            this.completionRate = 0.0;
            this.averageDeliveryValue = BigDecimal.ZERO;
        }
    }
}
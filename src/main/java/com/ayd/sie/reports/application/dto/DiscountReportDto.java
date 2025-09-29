package com.ayd.sie.reports.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Discount report showing discounts applied by loyalty level")
public class DiscountReportDto {

    @JsonProperty("business_id")
    @Schema(description = "Business unique identifier", example = "456")
    private Integer businessId;

    @JsonProperty("business_name")
    @Schema(description = "Name of the business", example = "Tienda Electrónica")
    private String businessName;

    @JsonProperty("loyalty_level")
    @Schema(description = "Current loyalty level", example = "Oro")
    private String loyaltyLevel;

    @JsonProperty("period_start")
    @Schema(description = "Start date of the reporting period", example = "2025-01-01")
    private LocalDate periodStart;

    @JsonProperty("period_end")
    @Schema(description = "End date of the reporting period", example = "2025-01-31")
    private LocalDate periodEnd;

    @JsonProperty("total_deliveries")
    @Schema(description = "Total number of deliveries", example = "25")
    private Integer totalDeliveries;

    @JsonProperty("completed_deliveries")
    @Schema(description = "Number of completed deliveries", example = "23")
    private Integer completedDeliveries;

    @JsonProperty("cancelled_deliveries")
    @Schema(description = "Number of cancelled deliveries", example = "2")
    private Integer cancelledDeliveries;

    @JsonProperty("total_amount")
    @Schema(description = "Total amount before discounts", example = "2500.00")
    private BigDecimal totalAmount;

    @JsonProperty("discount_percentage")
    @Schema(description = "Discount percentage applied", example = "8.00")
    private BigDecimal discountPercentage;

    @JsonProperty("discount_amount")
    @Schema(description = "Total discount amount", example = "200.00")
    private BigDecimal discountAmount;

    @JsonProperty("final_amount")
    @Schema(description = "Final amount after discounts", example = "2300.00")
    private BigDecimal finalAmount;

    @JsonProperty("free_cancellations_used")
    @Schema(description = "Number of free cancellations used", example = "0")
    private Integer freeCancellationsUsed;

    @JsonProperty("penalty_amount")
    @Schema(description = "Total penalty amount for cancellations", example = "0.00")
    private BigDecimal penaltyAmount;
}
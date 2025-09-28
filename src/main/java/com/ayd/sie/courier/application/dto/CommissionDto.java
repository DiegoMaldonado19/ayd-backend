package com.ayd.sie.courier.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Courier commission information")
public class CommissionDto {

    @Schema(description = "Commission period start date", example = "2024-09-01")
    @JsonProperty("period_start")
    private LocalDate periodStart;

    @Schema(description = "Commission period end date", example = "2024-09-30")
    @JsonProperty("period_end")
    private LocalDate periodEnd;

    @Schema(description = "Total deliveries completed", example = "25")
    @JsonProperty("total_deliveries")
    private Integer totalDeliveries;

    @Schema(description = "Total commissions earned", example = "850.50")
    @JsonProperty("total_commissions")
    private BigDecimal totalCommissions;

    @Schema(description = "Total penalties applied", example = "50.00")
    @JsonProperty("total_penalties")
    private BigDecimal totalPenalties;

    @Schema(description = "Net total to receive", example = "800.50")
    @JsonProperty("net_total")
    private BigDecimal netTotal;

    @Schema(description = "Settlement status", example = "Pendiente", allowableValues = { "Pendiente", "Aprobada",
            "Pagada" })
    @JsonProperty("settlement_status")
    private String settlementStatus;

    @Schema(description = "Payment date (if paid)", example = "2024-10-05T10:30:00")
    @JsonProperty("payment_date")
    private LocalDateTime paymentDate;

    @Schema(description = "Detailed commission breakdown")
    @JsonProperty("delivery_details")
    private List<DeliveryCommissionDetail> deliveryDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Individual delivery commission detail")
    public static class DeliveryCommissionDetail {

        @JsonProperty("guide_id")
        private Integer guideId;

        @JsonProperty("guide_number")
        private String guideNumber;

        @JsonProperty("business_name")
        private String businessName;

        @JsonProperty("delivery_date")
        private LocalDateTime deliveryDate;

        @JsonProperty("base_price")
        private BigDecimal basePrice;

        @JsonProperty("commission_rate")
        private BigDecimal commissionRate;

        @JsonProperty("commission_amount")
        private BigDecimal commissionAmount;

        @JsonProperty("delivery_status")
        private String deliveryStatus;

        @JsonProperty("recipient_name")
        private String recipientName;
    }
}
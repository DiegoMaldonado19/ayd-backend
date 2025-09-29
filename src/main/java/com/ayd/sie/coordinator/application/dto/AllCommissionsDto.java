package com.ayd.sie.coordinator.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "All commissions data transfer object for coordinators")
public class AllCommissionsDto {

    @Schema(description = "Guide ID", example = "1")
    @JsonProperty("guide_id")
    private Integer guideId;

    @Schema(description = "Guide number", example = "202500000001")
    @JsonProperty("guide_number")
    private String guideNumber;

    @Schema(description = "Courier ID", example = "6")
    @JsonProperty("courier_id")
    private Integer courierId;

    @Schema(description = "Courier full name", example = "Carlos Eduardo Morales Cruz")
    @JsonProperty("courier_name")
    private String courierName;

    @Schema(description = "Business name", example = "Electronica Moderna")
    @JsonProperty("business_name")
    private String businessName;

    @Schema(description = "Recipient name", example = "Maria Fernanda Lopez")
    @JsonProperty("recipient_name")
    private String recipientName;

    @Schema(description = "Delivery date", example = "2024-09-27T14:30:00")
    @JsonProperty("delivery_date")
    private LocalDateTime deliveryDate;

    @Schema(description = "Base price for the delivery", example = "45.00")
    @JsonProperty("base_price")
    private BigDecimal basePrice;

    @Schema(description = "Commission rate applied", example = "0.15")
    @JsonProperty("commission_rate")
    private BigDecimal commissionRate;

    @Schema(description = "Commission amount earned", example = "6.75")
    @JsonProperty("commission_amount")
    private BigDecimal commissionAmount;

    @Schema(description = "Current status", example = "Entregada")
    @JsonProperty("current_status")
    private String currentStatus;

    @Schema(description = "Settlement status", example = "Pendiente", allowableValues = { "Pendiente", "Aprobada",
            "Pagada" })
    @JsonProperty("settlement_status")
    private String settlementStatus;
}
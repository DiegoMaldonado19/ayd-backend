package com.ayd.sie.courier.application.dto;

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
@Schema(description = "Courier delivery information")
public class CourierDeliveryDto {

    @Schema(description = "Guide ID", example = "5")
    @JsonProperty("guide_id")
    private Integer guideId;

    @Schema(description = "Guide number", example = "202400000005")
    @JsonProperty("guide_number")
    private String guideNumber;

    @Schema(description = "Business name", example = "Electronica Moderna")
    @JsonProperty("business_name")
    private String businessName;

    @Schema(description = "Current delivery state", example = "Asignada")
    @JsonProperty("current_state")
    private String currentState;

    @Schema(description = "Base delivery price", example = "45.00")
    @JsonProperty("base_price")
    private BigDecimal basePrice;

    @Schema(description = "Courier commission", example = "13.50")
    @JsonProperty("courier_commission")
    private BigDecimal courierCommission;

    @Schema(description = "Recipient name", example = "Maria Fernanda Lopez")
    @JsonProperty("recipient_name")
    private String recipientName;

    @Schema(description = "Recipient phone", example = "55881122")
    @JsonProperty("recipient_phone")
    private String recipientPhone;

    @Schema(description = "Recipient address", example = "Quinta Calle 23-45 Zona 5")
    @JsonProperty("recipient_address")
    private String recipientAddress;

    @Schema(description = "Recipient city", example = "Guatemala")
    @JsonProperty("recipient_city")
    private String recipientCity;

    @Schema(description = "Recipient state", example = "Guatemala")
    @JsonProperty("recipient_state")
    private String recipientState;

    @Schema(description = "Delivery observations", example = "Entregar en horario matutino")
    @JsonProperty("observations")
    private String observations;

    @Schema(description = "Assignment accepted", example = "true")
    @JsonProperty("assignment_accepted")
    private Boolean assignmentAccepted;

    @Schema(description = "Assignment date", example = "2024-09-28T08:30:00")
    @JsonProperty("assignment_date")
    private LocalDateTime assignmentDate;

    @Schema(description = "Assignment accepted at", example = "2024-09-28T09:15:00")
    @JsonProperty("assignment_accepted_at")
    private LocalDateTime assignmentAcceptedAt;

    @Schema(description = "Pickup date", example = "2024-09-28T10:30:00")
    @JsonProperty("pickup_date")
    private LocalDateTime pickupDate;

    @Schema(description = "Delivery date", example = "2024-09-28T15:45:00")
    @JsonProperty("delivery_date")
    private LocalDateTime deliveryDate;

    @Schema(description = "Creation date", example = "2024-09-28T08:00:00")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "Priority level", example = "NORMAL")
    @JsonProperty("priority")
    private String priority;

    @Schema(description = "Has incidents", example = "false")
    @JsonProperty("has_incidents")
    private Boolean hasIncidents;
}
package com.ayd.sie.coordinator.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Schema(description = "Assignment data transfer object")
public class AssignmentDto {

    @Schema(description = "Assignment ID", example = "1")
    @JsonProperty("assignment_id")
    private Integer assignmentId;

    @Schema(description = "Guide ID to assign", example = "1", required = true)
    @JsonProperty("guide_id")
    @NotNull(message = "Guide ID is required")
    @Positive(message = "Guide ID must be positive")
    private Integer guideId;

    @Schema(description = "Guide number", example = "SIE202409001")
    @JsonProperty("guide_number")
    private String guideNumber;

    @Schema(description = "Courier ID to assign to", example = "6", required = true)
    @JsonProperty("courier_id")
    @NotNull(message = "Courier ID is required")
    @Positive(message = "Courier ID must be positive")
    private Integer courierId;

    @Schema(description = "Courier full name", example = "Carlos Eduardo Morales Cruz")
    @JsonProperty("courier_name")
    private String courierName;

    @Schema(description = "Coordinator ID who made the assignment", example = "3")
    @JsonProperty("coordinator_id")
    private Integer coordinatorId;

    @Schema(description = "Coordinator full name", example = "Ana Sofia Rodriguez Martinez")
    @JsonProperty("coordinator_name")
    private String coordinatorName;

    @Schema(description = "Assignment criteria used", example = "ZONE_PROXIMITY")
    @JsonProperty("assignment_criteria")
    private String assignmentCriteria;

    @Schema(description = "Base price for the delivery", example = "45.00")
    @JsonProperty("base_price")
    private BigDecimal basePrice;

    @Schema(description = "Calculated courier commission", example = "13.50")
    @JsonProperty("courier_commission")
    private BigDecimal courierCommission;

    @Schema(description = "Assignment timestamp", example = "2024-09-27T10:30:00")
    @JsonProperty("assigned_at")
    private LocalDateTime assignedAt;

    @Schema(description = "Whether assignment was accepted by courier", example = "true")
    @JsonProperty("assignment_accepted")
    private Boolean assignmentAccepted;

    @Schema(description = "When assignment was accepted", example = "2024-09-27T10:35:00")
    @JsonProperty("assignment_accepted_at")
    private LocalDateTime assignmentAcceptedAt;

    @Schema(description = "Business name", example = "Electronica Moderna")
    @JsonProperty("business_name")
    private String businessName;

    @Schema(description = "Recipient name", example = "Maria Fernanda Lopez")
    @JsonProperty("recipient_name")
    private String recipientName;

    @Schema(description = "Recipient address", example = "Quinta Calle 23-45 Zona 5")
    @JsonProperty("recipient_address")
    private String recipientAddress;

    @Schema(description = "Current state", example = "Asignada")
    @JsonProperty("current_state")
    private String currentState;

    @Schema(description = "Assignment observations", example = "Priority delivery - fragile package")
    @JsonProperty("observations")
    private String observations;
}
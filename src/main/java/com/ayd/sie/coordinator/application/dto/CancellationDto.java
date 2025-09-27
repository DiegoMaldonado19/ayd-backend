package com.ayd.sie.coordinator.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cancellation data transfer object")
public class CancellationDto {

    @Schema(description = "Cancellation ID", example = "1")
    @JsonProperty("cancellation_id")
    private Integer cancellationId;

    @Schema(description = "Guide ID to cancel", example = "7", required = true)
    @JsonProperty("guide_id")
    @NotNull(message = "Guide ID is required")
    @Positive(message = "Guide ID must be positive")
    private Integer guideId;

    @Schema(description = "Guide number", example = "SIE202409007")
    @JsonProperty("guide_number")
    private String guideNumber;

    @Schema(description = "Cancellation type ID", example = "1", required = true)
    @JsonProperty("cancellation_type_id")
    @NotNull(message = "Cancellation type ID is required")
    @Positive(message = "Cancellation type ID must be positive")
    private Integer cancellationTypeId;

    @Schema(description = "Cancellation type name", example = "Comercio")
    @JsonProperty("cancellation_type_name")
    private String cancellationTypeName;

    @Schema(description = "Reason for cancellation", example = "Cliente cambio de opinion sobre el producto", required = true)
    @JsonProperty("reason")
    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;

    @Schema(description = "Penalty amount applied", example = "15.75")
    @JsonProperty("penalty_amount")
    private Double penaltyAmount;

    @Schema(description = "Courier commission for cancelled delivery", example = "6.75")
    @JsonProperty("courier_commission")
    private Double courierCommission;

    @Schema(description = "Name of user who cancelled", example = "Ana Sofia Rodriguez Martinez")
    @JsonProperty("cancelled_by_name")
    private String cancelledByName;

    @Schema(description = "When the cancellation was requested", example = "2024-09-27T14:30:00")
    @JsonProperty("cancelled_at")
    private LocalDateTime cancelledAt;

    @Schema(description = "When the cancellation was processed", example = "2024-09-27T14:35:00")
    @JsonProperty("processed_at")
    private LocalDateTime processedAt;

    @Schema(description = "Business name", example = "Electronica Moderna")
    @JsonProperty("business_name")
    private String businessName;

    @Schema(description = "Coordinator notes", example = "Cancellation approved due to client change of mind")
    @JsonProperty("coordinator_notes")
    private String coordinatorNotes;

    @Schema(description = "Base price of the cancelled delivery", example = "45.00")
    @JsonProperty("base_price")
    private Double basePrice;

    @Schema(description = "Current delivery status", example = "Asignada")
    @JsonProperty("current_state")
    private String currentState;

    @Schema(description = "Assigned courier name", example = "Carlos Eduardo Morales Cruz")
    @JsonProperty("assigned_courier_name")
    private String assignedCourierName;

    @Schema(description = "Recipient name", example = "Maria Fernanda Lopez")
    @JsonProperty("recipient_name")
    private String recipientName;

    @Schema(description = "Recipient address", example = "Quinta Calle 23-45 Zona 5")
    @JsonProperty("recipient_address")
    private String recipientAddress;
}
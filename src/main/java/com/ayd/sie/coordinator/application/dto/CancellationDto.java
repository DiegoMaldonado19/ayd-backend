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
    @NotBlank(message = "Cancellation reason is required")
    @Size(min = 10, max = 300, message = "Reason must be between 10 and 300 characters")
    private String reason;

    @Schema(description = "User ID who requested cancellation", example = "13")
    @JsonProperty("requested_by_user_id")
    private Integer requestedByUserId;

    @Schema(description = "Requester full name", example = "Ricardo Alberto Mendez Silva")
    @JsonProperty("requested_by_name")
    private String requestedByName;

    @Schema(description = "Requester role", example = "Comercio")
    @JsonProperty("requested_by_role")
    private String requestedByRole;

    @Schema(description = "Coordinator ID who processed cancellation", example = "4")
    @JsonProperty("processed_by_coordinator_id")
    private Integer processedByCoordinatorId;

    @Schema(description = "Coordinator name", example = "Luis Fernando Herrera Gonzalez")
    @JsonProperty("processed_by_coordinator_name")
    private String processedByCoordinatorName;

    @Schema(description = "Courier ID assigned to the guide", example = "6")
    @JsonProperty("courier_id")
    private Integer courierId;

    @Schema(description = "Courier name", example = "Carlos Eduardo Morales Cruz")
    @JsonProperty("courier_name")
    private String courierName;

    @Schema(description = "Business ID", example = "3")
    @JsonProperty("business_id")
    private Integer businessId;

    @Schema(description = "Business name", example = "Libreria El Saber")
    @JsonProperty("business_name")
    private String businessName;

    @Schema(description = "Business loyalty level", example = "Plata")
    @JsonProperty("business_loyalty_level")
    private String businessLoyaltyLevel;

    @Schema(description = "Penalty percentage applied", example = "100.00")
    @JsonProperty("penalty_percentage")
    private Double penaltyPercentage;

    @Schema(description = "Commission penalty amount", example = "16.50")
    @JsonProperty("commission_penalty")
    private Double commissionPenalty;

    @Schema(description = "Base price of the guide", example = "55.00")
    @JsonProperty("base_price")
    private Double basePrice;

    @Schema(description = "Original courier commission", example = "16.50")
    @JsonProperty("original_commission")
    private Double originalCommission;

    @Schema(description = "State before cancellation", example = "Asignada")
    @JsonProperty("previous_state")
    private String previousState;

    @Schema(description = "Recipient name", example = "Carmen Sofia Ruiz")
    @JsonProperty("recipient_name")
    private String recipientName;

    @Schema(description = "Recipient address", example = "Decima Calle 5-15 Zona 10")
    @JsonProperty("recipient_address")
    private String recipientAddress;

    @Schema(description = "Cancellation timestamp", example = "2024-09-27T13:45:00")
    @JsonProperty("cancelled_at")
    private LocalDateTime cancelledAt;

    @Schema(description = "Whether customer was notified", example = "true")
    @JsonProperty("customer_notified")
    @Builder.Default
    private Boolean customerNotified = false;

    @Schema(description = "Whether courier was notified", example = "true")
    @JsonProperty("courier_notified")
    @Builder.Default
    private Boolean courierNotified = false;
}
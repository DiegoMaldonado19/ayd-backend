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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to process delivery cancellation")
public class ProcessCancellationRequestDto {

    @Schema(description = "Guide ID to cancel", example = "7", required = true)
    @JsonProperty("guide_id")
    @NotNull(message = "Guide ID is required")
    @Positive(message = "Guide ID must be positive")
    private Integer guideId;

    @Schema(description = "Cancellation type ID", example = "1", required = true)
    @JsonProperty("cancellation_type_id")
    @NotNull(message = "Cancellation type ID is required")
    @Positive(message = "Cancellation type ID must be positive")
    private Integer cancellationTypeId;

    @Schema(description = "Reason for cancellation", example = "Cliente cambio de opinion sobre el producto solicitado", required = true)
    @JsonProperty("reason")
    @NotBlank(message = "Cancellation reason is required")
    @Size(min = 10, max = 300, message = "Reason must be between 10 and 300 characters")
    private String reason;

    @Schema(description = "User ID who requested cancellation", example = "13")
    @JsonProperty("requested_by_user_id")
    @Positive(message = "Requested by user ID must be positive")
    private Integer requestedByUserId;

    @Schema(description = "Whether to apply penalty according to loyalty level", example = "true")
    @JsonProperty("apply_penalty")
    @Builder.Default
    private Boolean applyPenalty = true;

    @Schema(description = "Whether to notify the courier", example = "true")
    @JsonProperty("notify_courier")
    @Builder.Default
    private Boolean notifyCourier = true;

    @Schema(description = "Whether to notify the customer", example = "true")
    @JsonProperty("notify_customer")
    @Builder.Default
    private Boolean notifyCustomer = true;

    @Schema(description = "Whether to notify the business", example = "true")
    @JsonProperty("notify_business")
    @Builder.Default
    private Boolean notifyBusiness = true;

    @Schema(description = "Additional notes for cancellation", example = "Cancellation approved by coordinator due to customer request")
    @JsonProperty("notes")
    @Size(max = 300, message = "Notes cannot exceed 300 characters")
    private String notes;
}
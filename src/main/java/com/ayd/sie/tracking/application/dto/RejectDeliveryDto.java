package com.ayd.sie.tracking.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to reject a delivery")
public class RejectDeliveryDto {

    @JsonProperty("guide_number")
    @Schema(description = "Tracking guide number", example = "SIE202500001")
    @NotBlank(message = "Guide number is required")
    private String guideNumber;

    @JsonProperty("rejection_reason")
    @Schema(description = "Reason for rejecting the delivery", example = "Package damaged during transport")
    @NotBlank(message = "Rejection reason is required")
    @Size(max = 500, message = "Rejection reason must not exceed 500 characters")
    private String rejectionReason;

    @JsonProperty("requires_return")
    @Schema(description = "Whether the package should be returned to sender", example = "true")
    @NotNull(message = "Requires return flag is required")
    private Boolean requiresReturn;
}
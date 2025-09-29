package com.ayd.sie.tracking.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response after rejecting a delivery")
public class RejectDeliveryResponseDto {

    @JsonProperty("guide_number")
    @Schema(description = "Tracking guide number", example = "202500000001")
    private String guideNumber;

    @JsonProperty("status")
    @Schema(description = "New status after rejection", example = "Rechazada")
    private String status;

    @JsonProperty("message")
    @Schema(description = "Success message", example = "Delivery has been rejected successfully")
    private String message;

    @JsonProperty("return_process_initiated")
    @Schema(description = "Whether return process was initiated", example = "true")
    private Boolean returnProcessInitiated;
}
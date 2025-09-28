package com.ayd.sie.coordinator.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request to assign delivery to courier")
public class AssignDeliveryRequestDto {

    @Schema(description = "Guide ID to assign", example = "5", required = true)
    @JsonProperty("guide_id")
    @NotNull(message = "Guide ID is required")
    @Positive(message = "Guide ID must be positive")
    private Integer guideId;

    @Schema(description = "Courier ID to assign to", example = "7", required = true)
    @JsonProperty("courier_id")
    @NotNull(message = "Courier ID is required")
    @Positive(message = "Courier ID must be positive")
    private Integer courierId;

    @Schema(description = "Assignment criteria", example = "MANUAL_SELECTION")
    @JsonProperty("assignment_criteria")
    private String assignmentCriteria;

    @Schema(description = "Priority level", example = "NORMAL")
    @JsonProperty("priority")
    private String priority;

    @Schema(description = "Special observations for the courier", example = "Handle with care - fragile electronics")
    @JsonProperty("observations")
    @Size(max = 300, message = "Observations cannot exceed 300 characters")
    private String observations;

    @Schema(description = "Estimated delivery time in hours", example = "2")
    @JsonProperty("estimated_hours")
    @Positive(message = "Estimated hours must be positive")
    private Integer estimatedHours;

    @Schema(description = "Force assignment even if courier has high workload", example = "false")
    @JsonProperty("force_assignment")
    @Builder.Default
    private Boolean forceAssignment = false;
}
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
@Schema(description = "Request to report delivery incident")
public class ReportIncidentRequestDto {

    @Schema(description = "Guide ID where incident occurred", example = "4", required = true)
    @JsonProperty("guide_id")
    @NotNull(message = "Guide ID is required")
    @Positive(message = "Guide ID must be positive")
    private Integer guideId;

    @Schema(description = "Incident type ID", example = "2", required = true)
    @JsonProperty("incident_type_id")
    @NotNull(message = "Incident type ID is required")
    @Positive(message = "Incident type ID must be positive")
    private Integer incidentTypeId;

    @Schema(description = "Detailed description of the incident", example = "La direccion Boulevard Principal 10-20 no existe en la colonia indicada", required = true)
    @JsonProperty("description")
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @Schema(description = "Immediate action taken", example = "Attempted to contact customer by phone")
    @JsonProperty("immediate_action")
    @Size(max = 300, message = "Immediate action cannot exceed 300 characters")
    private String immediateAction;

    @Schema(description = "Suggested resolution", example = "Contact customer to verify correct address")
    @JsonProperty("suggested_resolution")
    @Size(max = 300, message = "Suggested resolution cannot exceed 300 characters")
    private String suggestedResolution;

    @Schema(description = "Severity level", example = "MEDIUM")
    @JsonProperty("severity")
    private String severity;

    @Schema(description = "Whether package should be returned immediately", example = "true")
    @JsonProperty("requires_immediate_return")
    @Builder.Default
    private Boolean requiresImmediateReturn = false;

    @Schema(description = "Customer contact attempted", example = "true")
    @JsonProperty("customer_contacted")
    @Builder.Default
    private Boolean customerContacted = false;
}
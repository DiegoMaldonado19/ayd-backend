package com.ayd.sie.courier.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request to update delivery state")
public class UpdateStateDto {

    @Schema(description = "Guide ID to update", example = "5", required = true)
    @JsonProperty("guide_id")
    @NotNull(message = "Guide ID is required")
    @Positive(message = "Guide ID must be positive")
    private Integer guideId;

    @Schema(description = "New state name", example = "Recogida", required = true, allowableValues = { "Recogida",
            "En Ruta", "Entregada", "Incidencia" })
    @JsonProperty("new_state")
    @NotNull(message = "New state is required")
    @Size(max = 50, message = "State name cannot exceed 50 characters")
    private String newState;

    @Schema(description = "State change observations", example = "Package picked up from branch")
    @JsonProperty("observations")
    @Size(max = 500, message = "Observations cannot exceed 500 characters")
    private String observations;

    @Schema(description = "Current courier location", example = "En camino al destino")
    @JsonProperty("current_location")
    @Size(max = 200, message = "Current location cannot exceed 200 characters")
    private String currentLocation;

    @Schema(description = "Action timestamp (optional, defaults to current time)", example = "2024-09-28T14:30:00")
    @JsonProperty("action_timestamp")
    private LocalDateTime actionTimestamp;

    @Schema(description = "For incidents: incident type ID", example = "2")
    @JsonProperty("incident_type_id")
    private Integer incidentTypeId;

    @Schema(description = "For incidents: incident description", example = "Customer not available at address")
    @JsonProperty("incident_description")
    @Size(max = 500, message = "Incident description cannot exceed 500 characters")
    private String incidentDescription;
}
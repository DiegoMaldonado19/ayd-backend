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
@Schema(description = "Incident data transfer object")
public class IncidentDto {

    @Schema(description = "Incident ID", example = "1")
    @JsonProperty("incident_id")
    private Integer incidentId;

    @Schema(description = "Guide ID", example = "4", required = true)
    @JsonProperty("guide_id")
    @NotNull(message = "Guide ID is required")
    @Positive(message = "Guide ID must be positive")
    private Integer guideId;

    @Schema(description = "Guide number", example = "SIE202409004")
    @JsonProperty("guide_number")
    private String guideNumber;

    @Schema(description = "Incident type ID", example = "2", required = true)
    @JsonProperty("incident_type_id")
    @NotNull(message = "Incident type ID is required")
    @Positive(message = "Incident type ID must be positive")
    private Integer incidentTypeId;

    @Schema(description = "Incident type name", example = "Direccion Incorrecta")
    @JsonProperty("incident_type_name")
    private String incidentTypeName;

    @Schema(description = "Whether incident requires package return", example = "true")
    @JsonProperty("requires_return")
    private Boolean requiresReturn;

    @Schema(description = "User ID who reported the incident", example = "9")
    @JsonProperty("reported_by_user_id")
    private Integer reportedByUserId;

    @Schema(description = "Reporter full name", example = "Roberto Carlos Vasquez Soto")
    @JsonProperty("reported_by_name")
    private String reportedByName;

    @Schema(description = "Reporter role", example = "Repartidor")
    @JsonProperty("reported_by_role")
    private String reportedByRole;

    @Schema(description = "Incident description", example = "La direccion Boulevard Principal 10-20 no existe en la colonia indicada", required = true)
    @JsonProperty("description")
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Schema(description = "Resolution description", example = "Se contacto al cliente y proporciono direccion correcta")
    @JsonProperty("resolution")
    @Size(max = 500, message = "Resolution cannot exceed 500 characters")
    private String resolution;

    @Schema(description = "Whether incident is resolved", example = "true")
    @JsonProperty("resolved")
    @Builder.Default
    private Boolean resolved = false;

    @Schema(description = "Resolution timestamp", example = "2024-09-03T15:30:00")
    @JsonProperty("resolved_at")
    private LocalDateTime resolvedAt;

    @Schema(description = "User ID who resolved the incident", example = "4")
    @JsonProperty("resolved_by_user_id")
    private Integer resolvedByUserId;

    @Schema(description = "Resolver full name", example = "Luis Fernando Herrera Gonzalez")
    @JsonProperty("resolved_by_name")
    private String resolvedByName;

    @Schema(description = "Business name", example = "Farmacia Salud Total")
    @JsonProperty("business_name")
    private String businessName;

    @Schema(description = "Recipient name", example = "Pedro Jose Martinez")
    @JsonProperty("recipient_name")
    private String recipientName;

    @Schema(description = "Recipient address", example = "Boulevard Principal 10-20 Colonia Vista Hermosa")
    @JsonProperty("recipient_address")
    private String recipientAddress;

    @Schema(description = "Current guide state", example = "Incidencia")
    @JsonProperty("current_state")
    private String currentState;

    @Schema(description = "Incident creation timestamp", example = "2024-09-03T14:45:00")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-09-03T15:30:00")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
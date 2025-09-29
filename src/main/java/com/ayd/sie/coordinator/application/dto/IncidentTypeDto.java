package com.ayd.sie.coordinator.application.dto;

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
@Schema(description = "Incident type information")
public class IncidentTypeDto {

    @Schema(description = "Incident type ID", example = "1")
    @JsonProperty("incident_type_id")
    private Integer incidentTypeId;

    @Schema(description = "Incident type name", example = "Direccion Incorrecta")
    @JsonProperty("type_name")
    private String typeName;

    @Schema(description = "Incident type description", example = "La direccion proporcionada no corresponde al destino")
    @JsonProperty("description")
    private String description;

    @Schema(description = "Indicates if the incident requires package return", example = "true")
    @JsonProperty("requires_return")
    private Boolean requiresReturn;
}

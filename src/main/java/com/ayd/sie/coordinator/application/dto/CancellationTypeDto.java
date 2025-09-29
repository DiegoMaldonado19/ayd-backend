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
@Schema(description = "Cancellation type information")
public class CancellationTypeDto {

    @Schema(description = "Cancellation type ID", example = "1")
    @JsonProperty("cancellation_type_id")
    private Integer cancellationTypeId;

    @Schema(description = "Cancellation type name", example = "Cliente")
    @JsonProperty("type_name")
    private String typeName;

    @Schema(description = "Cancellation type description", example = "Cancelación solicitada por el cliente")
    @JsonProperty("description")
    private String description;
}

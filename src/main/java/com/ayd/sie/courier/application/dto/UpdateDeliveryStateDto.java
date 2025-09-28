package com.ayd.sie.courier.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for updating only the state of a delivery")
public class UpdateDeliveryStateDto {

    @Schema(description = "New state for the delivery", example = "Recogida", required = true, allowableValues = {
            "Recogida", "En Ruta", "Entregada", "Incidencia" })
    @JsonProperty("new_state")
    @NotBlank(message = "New state is required")
    private String newState;

    @Schema(description = "Optional observations for the state change", example = "Package collected successfully")
    @JsonProperty("observations")
    private String observations;
}
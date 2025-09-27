package com.ayd.sie.admin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update system configuration request")
public class UpdateSystemConfigRequestDto {

    @JsonProperty("config_value")
    @NotBlank(message = "Configuration value is required")
    @Size(max = 1000, message = "Configuration value must not exceed 1000 characters")
    @Schema(description = "Configuration value", example = "soporte@sie.com.gt")
    private String configValue;

    @JsonProperty("description")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Configuration description", example = "Email de contacto para soporte")
    private String description;
}
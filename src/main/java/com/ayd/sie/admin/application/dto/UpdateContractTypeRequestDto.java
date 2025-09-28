package com.ayd.sie.admin.application.dto;

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
@Schema(description = "Request to update a contract type")
public class UpdateContractTypeRequestDto {

    @NotBlank(message = "Type name is required")
    @Size(max = 50, message = "Type name must not exceed 50 characters")
    @Schema(description = "Updated contract type name", example = "Permanente Actualizado")
    private String type_name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Updated contract type description", example = "Contrato permanente con beneficios")
    private String description;

    @Schema(description = "Contract type active status", example = "true")
    private Boolean active;
}
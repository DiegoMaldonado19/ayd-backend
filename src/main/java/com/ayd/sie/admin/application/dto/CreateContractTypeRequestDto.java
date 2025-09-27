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
@Schema(description = "Request to create a new contract type")
public class CreateContractTypeRequestDto {

    @NotBlank(message = "Type name is required")
    @Size(max = 50, message = "Type name must not exceed 50 characters")
    @Schema(description = "Contract type name", example = "Permanente")
    private String type_name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Contract type description", example = "Contrato de trabajo permanente")
    private String description;
}
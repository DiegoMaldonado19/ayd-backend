package com.ayd.sie.admin.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Contract type information")
public class ContractTypeDto {

    @Schema(description = "Contract type ID", example = "1")
    private Integer contract_type_id;

    @Schema(description = "Contract type name", example = "Temporal")
    private String type_name;

    @Schema(description = "Contract type description", example = "Contrato por tiempo determinado")
    private String description;

    @Schema(description = "Whether the contract type is active", example = "true")
    private Boolean active;

    @Schema(description = "Creation timestamp")
    private LocalDateTime created_at;
}
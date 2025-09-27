package com.ayd.sie.admin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create contract request")
public class CreateContractRequestDto {

    @JsonProperty("user_id")
    @NotNull(message = "Employee is required")
    @Min(value = 1, message = "Invalid employee")
    @Schema(description = "Employee user ID", example = "6")
    private Integer userId;

    @JsonProperty("contract_type_id")
    @NotNull(message = "Contract type is required")
    @Min(value = 1, message = "Invalid contract type")
    @Schema(description = "Contract type ID", example = "2")
    private Integer contractTypeId;

    @JsonProperty("base_salary")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base salary must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Base salary too high")
    @Schema(description = "Base monthly salary (optional for commission-only contracts)", example = "3000.00")
    private BigDecimal baseSalary;

    @JsonProperty("commission_percentage")
    @NotNull(message = "Commission percentage is required")
    @DecimalMin(value = "0.0", message = "Commission percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Commission percentage cannot exceed 100")
    @Schema(description = "Commission percentage", example = "30.00")
    private BigDecimal commissionPercentage;

    @JsonProperty("start_date")
    @NotNull(message = "Start date is required")
    @Schema(description = "Contract start date", example = "2024-01-01")
    private LocalDate startDate;

    @JsonProperty("end_date")
    @Schema(description = "Contract end date (null for permanent contracts)", example = "2025-01-31")
    private LocalDate endDate;

    @JsonProperty("observations")
    @Size(max = 500, message = "Observations must not exceed 500 characters")
    @Schema(description = "Contract observations", example = "Contrato permanente con comisiones")
    private String observations;
}
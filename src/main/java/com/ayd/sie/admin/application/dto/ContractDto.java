package com.ayd.sie.admin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Contract information")
public class ContractDto {

    @JsonProperty("contract_id")
    @Schema(description = "Contract unique identifier")
    private Integer contractId;

    @JsonProperty("user_id")
    @Schema(description = "Employee user ID")
    private Integer userId;

    @JsonProperty("user_full_name")
    @Schema(description = "Employee full name")
    private String userFullName;

    @JsonProperty("user_email")
    @Schema(description = "Employee email")
    private String userEmail;

    @JsonProperty("admin_id")
    @Schema(description = "Admin who created the contract")
    private Integer adminId;

    @JsonProperty("admin_full_name")
    @Schema(description = "Admin full name")
    private String adminFullName;

    @JsonProperty("contract_type_id")
    @Schema(description = "Contract type ID")
    private Integer contractTypeId;

    @JsonProperty("contract_type_name")
    @Schema(description = "Contract type name")
    private String contractTypeName;

    @JsonProperty("base_salary")
    @Schema(description = "Base monthly salary")
    private BigDecimal baseSalary;

    @JsonProperty("commission_percentage")
    @Schema(description = "Commission percentage")
    private BigDecimal commissionPercentage;

    @JsonProperty("start_date")
    @Schema(description = "Contract start date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    @Schema(description = "Contract end date")
    private LocalDate endDate;

    @JsonProperty("active")
    @Schema(description = "Contract status")
    private Boolean active;

    @JsonProperty("observations")
    @Schema(description = "Contract observations")
    private String observations;

    @JsonProperty("created_at")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
package com.ayd.sie.admin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@Schema(description = "Branch information")
public class BranchDto {

    @JsonProperty("branch_id")
    @Schema(description = "Branch unique identifier")
    private Integer branchId;

    @JsonProperty("branch_code")
    @NotBlank(message = "Branch code is required")
    @Size(min = 3, max = 20, message = "Branch code must be between 3 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Branch code must contain only uppercase letters and numbers")
    @Schema(description = "Unique branch code", example = "SUC001")
    private String branchCode;

    @JsonProperty("branch_name")
    @NotBlank(message = "Branch name is required")
    @Size(min = 2, max = 100, message = "Branch name must be between 2 and 100 characters")
    @Schema(description = "Branch name", example = "Sucursal Central")
    private String branchName;

    @JsonProperty("address")
    @NotBlank(message = "Address is required")
    @Schema(description = "Branch address", example = "Avenida Reforma 15-45 Zona 10")
    private String address;

    @JsonProperty("phone")
    @Pattern(regexp = "^[0-9+\\-\\s()]{8,20}$", message = "Invalid phone format")
    @Schema(description = "Branch phone number", example = "23660000")
    private String phone;

    @JsonProperty("email")
    @Email(message = "Invalid email format")
    @Schema(description = "Branch email", example = "central@sie.com.gt")
    private String email;

    @JsonProperty("city")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Schema(description = "City", example = "Guatemala")
    private String city;

    @JsonProperty("state")
    @Size(max = 100, message = "State must not exceed 100 characters")
    @Schema(description = "State", example = "Guatemala")
    private String state;

    @JsonProperty("active")
    @Schema(description = "Branch status")
    private Boolean active;

    @JsonProperty("created_at")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
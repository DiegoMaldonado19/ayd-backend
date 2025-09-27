package com.ayd.sie.admin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(description = "Employee information")
public class EmployeeDto {

    @JsonProperty("user_id")
    @Schema(description = "Employee user ID")
    private Integer userId;

    @JsonProperty("role_id")
    @Schema(description = "Employee role ID")
    private Integer roleId;

    @JsonProperty("role_name")
    @Schema(description = "Employee role name")
    private String roleName;

    @JsonProperty("email")
    @Schema(description = "Employee email")
    private String email;

    @JsonProperty("first_name")
    @Schema(description = "Employee first name")
    private String firstName;

    @JsonProperty("last_name")
    @Schema(description = "Employee last name")
    private String lastName;

    @JsonProperty("full_name")
    @Schema(description = "Employee full name")
    private String fullName;

    @JsonProperty("phone")
    @Schema(description = "Employee phone")
    private String phone;

    @JsonProperty("address")
    @Schema(description = "Employee address")
    private String address;

    @JsonProperty("national_id")
    @Schema(description = "Employee national ID")
    private String nationalId;

    @JsonProperty("active")
    @Schema(description = "Employee status")
    private Boolean active;

    @JsonProperty("two_factor_enabled")
    @Schema(description = "Two-factor authentication status")
    private Boolean twoFactorEnabled;

    @JsonProperty("last_login")
    @Schema(description = "Last login timestamp")
    private LocalDateTime lastLogin;

    @JsonProperty("created_at")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @JsonProperty("has_active_contract")
    @Schema(description = "Indicates if employee has an active contract")
    private Boolean hasActiveContract;
}
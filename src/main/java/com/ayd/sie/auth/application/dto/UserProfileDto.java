package com.ayd.sie.auth.application.dto;

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
@Schema(description = "User profile information")
public class UserProfileDto {

    @JsonProperty("user_id")
    @Schema(description = "User unique identifier")
    private Integer userId;

    @JsonProperty("email")
    @Schema(description = "User email address")
    private String email;

    @JsonProperty("first_name")
    @Schema(description = "User first name")
    private String firstName;

    @JsonProperty("last_name")
    @Schema(description = "User last name")
    private String lastName;

    @JsonProperty("full_name")
    @Schema(description = "User full name")
    private String fullName;

    @JsonProperty("phone")
    @Schema(description = "User phone number")
    private String phone;

    @JsonProperty("role")
    @Schema(description = "User role")
    private String role;

    @JsonProperty("two_factor_enabled")
    @Schema(description = "Indicates if 2FA is enabled")
    private Boolean twoFactorEnabled;

    @JsonProperty("last_login")
    @Schema(description = "Last login timestamp")
    private LocalDateTime lastLogin;

    @JsonProperty("created_at")
    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;
}
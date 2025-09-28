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
@Schema(description = "Login response data")
public class LoginResponseDto {

    @JsonProperty("access_token")
    @Schema(description = "JWT access token for API authentication")
    private String accessToken;

    @JsonProperty("refresh_token")
    @Schema(description = "JWT refresh token for obtaining new access tokens")
    private String refreshToken;

    @JsonProperty("token_type")
    @Schema(description = "Type of token", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @JsonProperty("expires_in")
    @Schema(description = "Access token expiration time in seconds")
    private Long expiresIn;

    @JsonProperty("user_info")
    @Schema(description = "Basic user information")
    private UserInfoDto userInfo;

    @JsonProperty("two_factor_required")
    @Schema(description = "Indicates if two-factor authentication is required")
    private Boolean twoFactorRequired;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Basic user information")
    public static class UserInfoDto {

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
        @Schema(description = "Indicates if two-factor authentication is enabled")
        private Boolean twoFactorEnabled;

        @JsonProperty("last_login")
        @Schema(description = "Last login timestamp")
        private LocalDateTime lastLogin;

        @JsonProperty("created_at")
        @Schema(description = "Account creation timestamp")
        private LocalDateTime createdAt;
    }
}
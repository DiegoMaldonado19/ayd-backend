package com.ayd.sie.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Two-factor authentication verification request")
public class TwoFactorRequestDto {

    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Schema(description = "User email address", example = "admin.principal@sie.com.gt")
    private String email;

    @JsonProperty("verification_code")
    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Verification code must be exactly 6 digits")
    @Schema(description = "6-digit verification code", example = "123456")
    private String verificationCode;
}
package com.ayd.sie.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Disable 2FA request")
public class Disable2FARequestDto {

    @JsonProperty("password")
    @NotBlank(message = "Password is required to disable 2FA")
    @Schema(description = "Current password for verification", example = "Admin123!")
    private String password;

    @JsonProperty("verification_code")
    @NotBlank(message = "Current 2FA code is required")
    @Schema(description = "Current 2FA verification code", example = "123456")
    private String verificationCode;
}
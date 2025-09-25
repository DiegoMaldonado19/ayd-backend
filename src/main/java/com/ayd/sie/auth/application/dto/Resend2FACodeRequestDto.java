package com.ayd.sie.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resend 2FA code request")
public class Resend2FACodeRequestDto {

    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Schema(description = "User email address", example = "admin.principal@sie.com.gt")
    private String email;
}
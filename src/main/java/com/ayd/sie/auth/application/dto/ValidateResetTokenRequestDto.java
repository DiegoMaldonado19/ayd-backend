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
@Schema(description = "Validate reset token request")
public class ValidateResetTokenRequestDto {

    @JsonProperty("reset_token")
    @NotBlank(message = "Reset token is required")
    @Schema(description = "Password reset token to validate")
    private String resetToken;
}
package com.ayd.sie.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Enable 2FA response")
public class Enable2FAResponseDto {

    @JsonProperty("two_factor_enabled")
    @Schema(description = "Indicates if 2FA was successfully enabled")
    private Boolean twoFactorEnabled;

    @JsonProperty("message")
    @Schema(description = "Status message")
    private String message;
}
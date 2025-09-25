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
@Schema(description = "Validate reset token response")
public class ValidateResetTokenResponseDto {

    @JsonProperty("is_valid")
    @Schema(description = "Indicates if the token is valid")
    private Boolean isValid;

    @JsonProperty("expires_in_minutes")
    @Schema(description = "Minutes until token expires (if valid)")
    private Long expiresInMinutes;

    @JsonProperty("email")
    @Schema(description = "Email associated with the token (if valid)")
    private String email;
}
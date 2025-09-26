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
@Schema(description = "Validate token response")
public class ValidateTokenResponseDto {

    @JsonProperty("is_valid")
    @Schema(description = "Indicates if the token is valid")
    private Boolean isValid;

    @JsonProperty("expires_at")
    @Schema(description = "Token expiration timestamp")
    private LocalDateTime expiresAt;

    @JsonProperty("user_email")
    @Schema(description = "Email associated with the token")
    private String userEmail;

    @JsonProperty("user_role")
    @Schema(description = "Role associated with the token")
    private String userRole;
}
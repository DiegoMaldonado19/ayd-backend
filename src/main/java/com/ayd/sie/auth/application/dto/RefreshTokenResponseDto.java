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
@Schema(description = "Refresh token response")
public class RefreshTokenResponseDto {

    @JsonProperty("access_token")
    @Schema(description = "New JWT access token")
    private String accessToken;

    @JsonProperty("refresh_token")
    @Schema(description = "New refresh token (optional)")
    private String refreshToken;

    @JsonProperty("token_type")
    @Schema(description = "Type of token", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @JsonProperty("expires_in")
    @Schema(description = "Access token expiration time in seconds")
    private Long expiresIn;
}
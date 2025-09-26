package com.ayd.sie.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Revoke token request")
public class RevokeTokenRequestDto {

    @JsonProperty("token_id")
    @NotNull(message = "Token ID is required")
    @Schema(description = "ID of the token to revoke")
    private Integer tokenId;
}
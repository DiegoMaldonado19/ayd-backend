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
@Schema(description = "User session information")
public class UserSessionDto {

    @JsonProperty("token_id")
    @Schema(description = "Token unique identifier")
    private Integer tokenId;

    @JsonProperty("ip_address")
    @Schema(description = "IP address of the session")
    private String ipAddress;

    @JsonProperty("user_agent")
    @Schema(description = "User agent information")
    private String userAgent;

    @JsonProperty("created_at")
    @Schema(description = "Session creation timestamp")
    private LocalDateTime createdAt;

    @JsonProperty("expires_at")
    @Schema(description = "Session expiration timestamp")
    private LocalDateTime expiresAt;

    @JsonProperty("is_current")
    @Schema(description = "Indicates if this is the current session")
    private Boolean isCurrent;
}
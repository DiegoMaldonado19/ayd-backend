package com.ayd.sie.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "List user sessions response")
public class ListUserSessionsResponseDto {

    @JsonProperty("active_sessions")
    @Schema(description = "List of active user sessions")
    private List<UserSessionDto> activeSessions;

    @JsonProperty("total_count")
    @Schema(description = "Total number of active sessions")
    private Integer totalCount;
}
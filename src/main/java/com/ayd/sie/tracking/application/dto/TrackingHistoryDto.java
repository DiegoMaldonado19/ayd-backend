package com.ayd.sie.tracking.application.dto;

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
@Schema(description = "Status history entry for tracking")
public class TrackingHistoryDto {

    @JsonProperty("status_name")
    @Schema(description = "Status name", example = "En Ruta")
    private String statusName;

    @JsonProperty("changed_at")
    @Schema(description = "When the status changed")
    private LocalDateTime changedAt;

    @JsonProperty("changed_by")
    @Schema(description = "Who changed the status", example = "Pedro González")
    private String changedBy;

    @JsonProperty("observations")
    @Schema(description = "Observations for this status change", example = "Package picked up successfully")
    private String observations;
}
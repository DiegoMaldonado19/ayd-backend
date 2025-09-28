package com.ayd.sie.courier.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to accept delivery assignment")
public class AcceptAssignmentDto {

    @Schema(description = "Guide ID to accept", example = "5", required = true)
    @JsonProperty("guide_id")
    @NotNull(message = "Guide ID is required")
    @Positive(message = "Guide ID must be positive")
    private Integer guideId;

    @Schema(description = "Courier acceptance notes", example = "Ready to start delivery")
    @JsonProperty("acceptance_notes")
    @Size(max = 300, message = "Acceptance notes cannot exceed 300 characters")
    private String acceptanceNotes;

    @Schema(description = "Estimated pickup time", example = "2024-09-28T10:30:00")
    @JsonProperty("estimated_pickup_time")
    private LocalDateTime estimatedPickupTime;

    @Schema(description = "Current courier location (optional)", example = "Near origin branch")
    @JsonProperty("current_location")
    @Size(max = 200, message = "Current location cannot exceed 200 characters")
    private String currentLocation;

    @Schema(description = "When the assignment was accepted", example = "2024-09-28T10:30:00")
    @JsonProperty("accepted_at")
    private LocalDateTime acceptedAt;
}
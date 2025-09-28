package com.ayd.sie.courier.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Request to reject delivery assignment")
public class RejectAssignmentDto {

    @Schema(description = "Guide ID to reject", example = "5", required = true)
    @JsonProperty("guide_id")
    @NotNull(message = "Guide ID is required")
    @Positive(message = "Guide ID must be positive")
    private Integer guideId;

    @Schema(description = "Reason for rejection", example = "Vehicle maintenance required", required = true)
    @JsonProperty("rejection_reason")
    @NotBlank(message = "Rejection reason is required")
    @Size(min = 10, max = 500, message = "Rejection reason must be between 10 and 500 characters")
    private String rejectionReason;

    @Schema(description = "Additional details about the rejection", example = "Will be available after 2pm")
    @JsonProperty("additional_notes")
    @Size(max = 300, message = "Additional notes cannot exceed 300 characters")
    private String additionalNotes;

    @Schema(description = "Current courier location (optional)", example = "Near central depot")
    @JsonProperty("current_location")
    @Size(max = 200, message = "Current location cannot exceed 200 characters")
    private String currentLocation;

    @Schema(description = "When the assignment was rejected", example = "2024-09-28T10:30:00")
    @JsonProperty("rejected_at")
    private LocalDateTime rejectedAt;

    @Schema(description = "Indicates if courier is unavailable for reassignment", example = "false")
    @JsonProperty("unavailable_for_reassignment")
    @Builder.Default
    private Boolean unavailableForReassignment = false;
}
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to report delivery incident")
public class ReportIncidentDto {

    @Schema(description = "Incident ID (for updates)", example = "1")
    @JsonProperty("incident_id")
    private Integer incidentId;

    @Schema(description = "Guide ID", example = "5", required = true)
    @JsonProperty("guide_id")
    @NotNull(message = "Guide ID is required")
    @Positive(message = "Guide ID must be positive")
    private Integer guideId;

    @Schema(description = "Incident type", example = "Cliente no disponible", required = true)
    @JsonProperty("incident_type")
    @NotNull(message = "Incident type is required")
    @Size(min = 5, max = 100, message = "Incident type must be between 5 and 100 characters")
    private String incidentType;

    @Schema(description = "Incident description", example = "Customer not available at address", required = true)
    @JsonProperty("description")
    @NotNull(message = "Incident description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @Schema(description = "Location latitude", example = "14.6349")
    @JsonProperty("latitude")
    private BigDecimal latitude;

    @Schema(description = "Location longitude", example = "-90.5069")
    @JsonProperty("longitude")
    private BigDecimal longitude;

    @Schema(description = "When the incident was reported")
    @JsonProperty("reported_at")
    private LocalDateTime reportedAt;

    @Schema(description = "Whether the incident is resolved")
    @JsonProperty("resolved")
    @Builder.Default
    private Boolean resolved = false;
}
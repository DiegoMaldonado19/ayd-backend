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
@Schema(description = "Delivery evidence registration")
public class EvidenceDto {

    @Schema(description = "Evidence ID (for updates)", example = "12")
    @JsonProperty("evidence_id")
    private Integer evidenceId;

    @Schema(description = "Guide ID", example = "5", required = true)
    @JsonProperty("guide_id")
    @NotNull(message = "Guide ID is required")
    @Positive(message = "Guide ID must be positive")
    private Integer guideId;

    @Schema(description = "Evidence type ID", example = "1", required = true, allowableValues = { "1", "2", "3" })
    @JsonProperty("evidence_type_id")
    @NotNull(message = "Evidence type ID is required")
    @Positive(message = "Evidence type ID must be positive")
    private Integer evidenceTypeId;

    @Schema(description = "Evidence type name", example = "Fotografia")
    @JsonProperty("evidence_type_name")
    private String evidenceTypeName;

    @Schema(description = "File URL or path to evidence", example = "/uploads/evidence/2024/09/guide5_photo.jpg")
    @JsonProperty("file_url")
    @Size(max = 500, message = "File URL cannot exceed 500 characters")
    private String fileUrl;

    @Schema(description = "Evidence notes", example = "Photo taken at delivery point with recipient")
    @JsonProperty("notes")
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @Schema(description = "Evidence creation timestamp", example = "2024-09-28T15:45:00")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "Evidence last update timestamp", example = "2024-09-28T15:50:00")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
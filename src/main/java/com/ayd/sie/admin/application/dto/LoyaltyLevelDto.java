package com.ayd.sie.admin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Loyalty level information")
public class LoyaltyLevelDto {

    @JsonProperty("level_id")
    @Schema(description = "Loyalty level unique identifier")
    private Integer levelId;

    @JsonProperty("level_name")
    @Schema(description = "Level name")
    private String levelName;

    @JsonProperty("min_deliveries")
    @Schema(description = "Minimum deliveries required")
    private Integer minDeliveries;

    @JsonProperty("max_deliveries")
    @Schema(description = "Maximum deliveries for this level")
    private Integer maxDeliveries;

    @JsonProperty("discount_percentage")
    @Schema(description = "Discount percentage")
    private BigDecimal discountPercentage;

    @JsonProperty("free_cancellations")
    @Schema(description = "Free cancellations per month")
    private Integer freeCancellations;

    @JsonProperty("penalty_percentage")
    @Schema(description = "Penalty percentage for cancellations")
    private BigDecimal penaltyPercentage;

    @JsonProperty("active")
    @Schema(description = "Level status")
    private Boolean active;

    @JsonProperty("created_at")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
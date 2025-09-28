package com.ayd.sie.admin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update loyalty level request")
public class UpdateLoyaltyLevelRequestDto {

    @JsonProperty("level_name")
    @NotBlank(message = "Level name is required")
    @Size(min = 2, max = 50, message = "Level name must be between 2 and 50 characters")
    @Schema(description = "Level name", example = "Plata")
    private String levelName;

    @JsonProperty("min_deliveries")
    @NotNull(message = "Minimum deliveries is required")
    @Min(value = 0, message = "Minimum deliveries must be at least 0")
    @Schema(description = "Minimum deliveries required", example = "0")
    private Integer minDeliveries;

    @JsonProperty("max_deliveries")
    @Min(value = 1, message = "Maximum deliveries must be at least 1")
    @Schema(description = "Maximum deliveries for this level (null for unlimited)", example = "99")
    private Integer maxDeliveries;

    @JsonProperty("discount_percentage")
    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0.0", message = "Discount percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Discount percentage cannot exceed 100")
    @Schema(description = "Discount percentage", example = "5.00")
    private BigDecimal discountPercentage;

    @JsonProperty("free_cancellations")
    @Min(value = 0, message = "Free cancellations must be at least 0")
    @Schema(description = "Free cancellations per month", example = "0")
    private Integer freeCancellations;

    @JsonProperty("penalty_percentage")
    @NotNull(message = "Penalty percentage is required")
    @DecimalMin(value = "0.0", message = "Penalty percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Penalty percentage cannot exceed 100")
    @Schema(description = "Penalty percentage for cancellations", example = "100.00")
    private BigDecimal penaltyPercentage;
}
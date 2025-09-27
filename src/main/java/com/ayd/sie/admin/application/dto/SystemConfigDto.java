package com.ayd.sie.admin.application.dto;

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
@Schema(description = "System configuration")
public class SystemConfigDto {

    @JsonProperty("config_id")
    @Schema(description = "Configuration unique identifier")
    private Integer configId;

    @JsonProperty("config_key")
    @Schema(description = "Configuration key")
    private String configKey;

    @JsonProperty("config_value")
    @Schema(description = "Configuration value")
    private String configValue;

    @JsonProperty("description")
    @Schema(description = "Configuration description")
    private String description;

    @JsonProperty("created_at")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
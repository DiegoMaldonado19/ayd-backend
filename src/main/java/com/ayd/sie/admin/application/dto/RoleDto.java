package com.ayd.sie.admin.application.dto;

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
@Schema(description = "Role information")
public class RoleDto {

    @Schema(description = "Role ID", example = "1")
    private Integer role_id;

    @Schema(description = "Role name", example = "Administrator")
    private String role_name;

    @Schema(description = "Role description", example = "System administrator with full access")
    private String description;

    @Schema(description = "Whether the role is active", example = "true")
    private Boolean active;

    @Schema(description = "Creation timestamp")
    private LocalDateTime created_at;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updated_at;
}
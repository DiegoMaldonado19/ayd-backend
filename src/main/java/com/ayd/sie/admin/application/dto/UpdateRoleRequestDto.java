package com.ayd.sie.admin.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update a role")
public class UpdateRoleRequestDto {

    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name must not exceed 50 characters")
    @Schema(description = "Updated role name", example = "Senior Manager")
    private String role_name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Updated role description", example = "Manages complex operations")
    private String description;

    @Schema(description = "Role active status", example = "true")
    private Boolean active;
}
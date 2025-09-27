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
@Schema(description = "Request to create a new role")
public class CreateRoleRequestDto {

    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name must not exceed 50 characters")
    @Schema(description = "Role name", example = "Manager")
    private String role_name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Role description", example = "Manages daily operations")
    private String description;
}
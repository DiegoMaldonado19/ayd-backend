package com.ayd.sie.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Change password request")
public class ChangePasswordRequestDto {

    @JsonProperty("current_password")
    @NotBlank(message = "Current password is required")
    @Schema(description = "Current password", example = "OldPassword123!")
    private String currentPassword;

    @JsonProperty("new_password")
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "Password must contain at least one uppercase, one lowercase, one digit and one special character")
    @Schema(description = "New password", example = "NewPassword123!")
    private String newPassword;

    @JsonProperty("confirm_password")
    @NotBlank(message = "Confirm password is required")
    @Schema(description = "Password confirmation", example = "NewPassword123!")
    private String confirmPassword;
}
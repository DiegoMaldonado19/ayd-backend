package com.ayd.sie.admin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Employee registration request")
public class EmployeeRegistrationRequestDto {

    @JsonProperty("role_id")
    @NotNull(message = "Role is required")
    @Min(value = 1, message = "Invalid role")
    @Max(value = 5, message = "Invalid role")
    @Schema(description = "Employee role ID (2=Coordinator, 3=Courier)", example = "3")
    private Integer roleId;

    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "Employee email", example = "repartidor001@sie.com.gt")
    private String email;

    @JsonProperty("first_name")
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Schema(description = "Employee first name", example = "Pedro Antonio")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Schema(description = "Employee last name", example = "Gonzalez Ramirez")
    private String lastName;

    @JsonProperty("phone")
    @Pattern(regexp = "^[0-9+\\-\\s()]{8,20}$", message = "Invalid phone format")
    @Schema(description = "Employee phone", example = "56123456")
    private String phone;

    @JsonProperty("address")
    @Schema(description = "Employee address", example = "Sexta Avenida 7-89 Zona 6")
    private String address;

    @JsonProperty("national_id")
    @Pattern(regexp = "^[0-9]{13}$", message = "National ID must be exactly 13 digits")
    @Schema(description = "Employee national ID", example = "2987654321006")
    private String nationalId;

    @JsonProperty("temporary_password")
    @Size(min = 8, max = 50, message = "Temporary password must be between 8 and 50 characters")
    @Schema(description = "Temporary password (optional, will be generated if not provided)")
    private String temporaryPassword;
}
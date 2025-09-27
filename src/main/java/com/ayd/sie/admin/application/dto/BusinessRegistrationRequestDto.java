package com.ayd.sie.admin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Business registration request")
public class BusinessRegistrationRequestDto {

    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "Business user email", example = "tienda.electronica@gmail.com")
    private String email;

    @JsonProperty("first_name")
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Schema(description = "Contact first name", example = "Fernando Jose")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Schema(description = "Contact last name", example = "Castillo Mejia")
    private String lastName;

    @JsonProperty("phone")
    @Pattern(regexp = "^[0-9+\\-\\s()]{8,20}$", message = "Invalid phone format")
    @Schema(description = "Contact phone", example = "57123456")
    private String phone;

    @JsonProperty("address")
    @Schema(description = "Contact address", example = "Once Calle 12-34 Zona 11")
    private String address;

    @JsonProperty("national_id")
    @Pattern(regexp = "^[0-9]{13}$", message = "National ID must be exactly 13 digits")
    @Schema(description = "Contact national ID", example = "2987654321011")
    private String nationalId;

    @JsonProperty("tax_id")
    @NotBlank(message = "Tax ID is required")
    @Pattern(regexp = "^[0-9]{6}-[0-9K]$", message = "Tax ID format should be 123456-7")
    @Schema(description = "Business tax ID (NIT)", example = "123456-7")
    private String taxId;

    @JsonProperty("business_name")
    @NotBlank(message = "Business name is required")
    @Size(min = 2, max = 100, message = "Business name must be between 2 and 100 characters")
    @Schema(description = "Business trading name", example = "Electronica Moderna")
    private String businessName;

    @JsonProperty("legal_name")
    @NotBlank(message = "Legal name is required")
    @Size(min = 2, max = 100, message = "Legal name must be between 2 and 100 characters")
    @Schema(description = "Business legal name", example = "Electronica Moderna Sociedad Anonima")
    private String legalName;

    @JsonProperty("tax_address")
    @NotBlank(message = "Tax address is required")
    @Schema(description = "Business fiscal address", example = "Centro Comercial Plaza Mayor Local 45")
    private String taxAddress;

    @JsonProperty("business_phone")
    @Pattern(regexp = "^[0-9+\\-\\s()]{8,20}$", message = "Invalid phone format")
    @Schema(description = "Business phone", example = "23334444")
    private String businessPhone;

    @JsonProperty("business_email")
    @Email(message = "Invalid email format")
    @Schema(description = "Business email", example = "ventas@electronicamoderna.gt")
    private String businessEmail;

    @JsonProperty("support_contact")
    @Schema(description = "Support contact", example = "soporte@electronicamoderna.gt")
    private String supportContact;

    @JsonProperty("affiliation_date")
    @NotNull(message = "Affiliation date is required")
    @Schema(description = "Business affiliation date", example = "2024-01-15")
    private LocalDate affiliationDate;

    @JsonProperty("initial_level_id")
    @NotNull(message = "Initial loyalty level is required")
    @Min(value = 1, message = "Invalid loyalty level")
    @Schema(description = "Initial loyalty level ID", example = "1")
    private Integer initialLevelId;
}
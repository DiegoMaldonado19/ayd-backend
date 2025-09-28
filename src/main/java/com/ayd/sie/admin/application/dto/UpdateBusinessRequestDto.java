package com.ayd.sie.admin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(description = "Update business request")
public class UpdateBusinessRequestDto {

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

    @JsonProperty("active")
    @Schema(description = "Business active status", example = "true")
    private Boolean active;
}
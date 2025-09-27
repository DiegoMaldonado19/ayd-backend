package com.ayd.sie.admin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Business information")
public class BusinessDto {

    @JsonProperty("business_id")
    @Schema(description = "Business unique identifier")
    private Integer businessId;

    @JsonProperty("user_id")
    @Schema(description = "Associated user ID")
    private Integer userId;

    @JsonProperty("user_email")
    @Schema(description = "User email")
    private String userEmail;

    @JsonProperty("user_full_name")
    @Schema(description = "Contact full name")
    private String userFullName;

    @JsonProperty("current_level_id")
    @Schema(description = "Current loyalty level ID")
    private Integer currentLevelId;

    @JsonProperty("current_level_name")
    @Schema(description = "Current loyalty level name")
    private String currentLevelName;

    @JsonProperty("tax_id")
    @Schema(description = "Business tax ID")
    private String taxId;

    @JsonProperty("business_name")
    @Schema(description = "Business trading name")
    private String businessName;

    @JsonProperty("legal_name")
    @Schema(description = "Business legal name")
    private String legalName;

    @JsonProperty("tax_address")
    @Schema(description = "Business fiscal address")
    private String taxAddress;

    @JsonProperty("business_phone")
    @Schema(description = "Business phone")
    private String businessPhone;

    @JsonProperty("business_email")
    @Schema(description = "Business email")
    private String businessEmail;

    @JsonProperty("support_contact")
    @Schema(description = "Support contact")
    private String supportContact;

    @JsonProperty("active")
    @Schema(description = "Business status")
    private Boolean active;

    @JsonProperty("affiliation_date")
    @Schema(description = "Affiliation date")
    private LocalDate affiliationDate;

    @JsonProperty("created_at")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
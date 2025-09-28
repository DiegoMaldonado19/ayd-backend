package com.ayd.sie.business.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGuideDto {

    @NotNull(message = "Branch ID is required")
    private Integer origin_branch_id;

    @NotNull(message = "Base price is required")
    private BigDecimal base_price;

    @NotBlank(message = "Recipient name is required")
    @Size(max = 100, message = "Recipient name cannot exceed 100 characters")
    private String recipient_name;

    @NotBlank(message = "Recipient phone is required")
    @Size(max = 20, message = "Recipient phone cannot exceed 20 characters")
    private String recipient_phone;

    @NotBlank(message = "Recipient address is required")
    private String recipient_address;

    @NotBlank(message = "Recipient city is required")
    @Size(max = 100, message = "Recipient city cannot exceed 100 characters")
    private String recipient_city;

    @NotBlank(message = "Recipient state is required")
    @Size(max = 100, message = "Recipient state cannot exceed 100 characters")
    private String recipient_state;

    private String observations;
}
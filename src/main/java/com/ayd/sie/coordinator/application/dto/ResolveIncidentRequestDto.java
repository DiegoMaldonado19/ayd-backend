package com.ayd.sie.coordinator.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(description = "Request to resolve delivery incident")
public class ResolveIncidentRequestDto {

    @Schema(description = "Resolution description", example = "Se contacto al cliente y proporciono direccion correcta: Calle Real 15-25 Zona 10", required = true)
    @JsonProperty("resolution")
    @NotBlank(message = "Resolution is required")
    @Size(min = 10, max = 500, message = "Resolution must be between 10 and 500 characters")
    private String resolution;

    @Schema(description = "Action taken to resolve", example = "ADDRESS_UPDATED")
    @JsonProperty("resolution_action")
    private String resolutionAction;

    @Schema(description = "New courier ID if reassignment is needed", example = "8")
    @JsonProperty("new_courier_id")
    private Integer newCourierId;

    @Schema(description = "Whether delivery should be rescheduled", example = "true")
    @JsonProperty("reschedule_delivery")
    @Builder.Default
    private Boolean rescheduleDelivery = false;

    @Schema(description = "Customer was contacted for resolution", example = "true")
    @JsonProperty("customer_contacted")
    @Builder.Default
    private Boolean customerContacted = false;

    @Schema(description = "Business was notified of resolution", example = "true")
    @JsonProperty("business_notified")
    @Builder.Default
    private Boolean businessNotified = false;

    @Schema(description = "Additional notes", example = "Customer confirmed new address and availability for delivery")
    @JsonProperty("notes")
    @Size(max = 300, message = "Notes cannot exceed 300 characters")
    private String notes;
}
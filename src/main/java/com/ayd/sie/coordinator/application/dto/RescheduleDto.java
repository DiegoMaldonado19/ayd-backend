package com.ayd.sie.coordinator.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reschedule data transfer object")
public class RescheduleDto {

    @Schema(description = "Reschedule ID", example = "1")
    @JsonProperty("reschedule_id")
    private Integer rescheduleId;

    @Schema(description = "Guide ID to reschedule", example = "5", required = true)
    @JsonProperty("guide_id")
    @NotNull(message = "Guide ID is required")
    @Positive(message = "Guide ID must be positive")
    private Integer guideId;

    @Schema(description = "Guide number", example = "SIE202409005")
    @JsonProperty("guide_number")
    private String guideNumber;

    @Schema(description = "New courier ID", example = "7")
    @JsonProperty("new_courier_id")
    @Positive(message = "New courier ID must be positive")
    private Integer newCourierId;

    @Schema(description = "New courier name", example = "Miguel Angel Perez Jimenez")
    @JsonProperty("new_courier_name")
    private String newCourierName;

    @Schema(description = "Previous courier ID", example = "6")
    @JsonProperty("previous_courier_id")
    private Integer previousCourierId;

    @Schema(description = "Previous courier name", example = "Carlos Eduardo Morales Cruz")
    @JsonProperty("previous_courier_name")
    private String previousCourierName;

    @Schema(description = "New scheduled delivery date", example = "2024-09-28T14:00:00")
    @JsonProperty("new_delivery_date")
    @Future(message = "New delivery date must be in the future")
    private LocalDateTime newDeliveryDate;

    @Schema(description = "Previous delivery date", example = "2024-09-27T14:00:00")
    @JsonProperty("previous_delivery_date")
    private LocalDateTime previousDeliveryDate;

    @Schema(description = "Reason for rescheduling", example = "Cliente solicita entrega en horario vespertino", required = true)
    @JsonProperty("reason")
    @Size(min = 10, max = 300, message = "Reason must be between 10 and 300 characters")
    private String reason;

    @Schema(description = "Coordinator ID who approved reschedule", example = "3")
    @JsonProperty("coordinator_id")
    private Integer coordinatorId;

    @Schema(description = "Coordinator name", example = "Ana Sofia Rodriguez Martinez")
    @JsonProperty("coordinator_name")
    private String coordinatorName;

    @Schema(description = "Business name", example = "Electronica Moderna")
    @JsonProperty("business_name")
    private String businessName;

    @Schema(description = "Recipient name", example = "Luisa Fernanda Perez")
    @JsonProperty("recipient_name")
    private String recipientName;

    @Schema(description = "Recipient phone", example = "55885566")
    @JsonProperty("recipient_phone")
    private String recipientPhone;

    @Schema(description = "Recipient address", example = "Calzada Roosevelt 25-30")
    @JsonProperty("recipient_address")
    private String recipientAddress;

    @Schema(description = "Current state after reschedule", example = "Asignada")
    @JsonProperty("current_state")
    private String currentState;

    @Schema(description = "Reschedule timestamp", example = "2024-09-27T11:30:00")
    @JsonProperty("rescheduled_at")
    private LocalDateTime rescheduledAt;

    @Schema(description = "Whether customer was notified", example = "true")
    @JsonProperty("customer_notified")
    @Builder.Default
    private Boolean customerNotified = false;

    @Schema(description = "Additional notes", example = "Customer confirmed availability for new time slot")
    @JsonProperty("notes")
    @Size(max = 300, message = "Notes cannot exceed 300 characters")
    private String notes;
}
package com.ayd.sie.tracking.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Public tracking information for a delivery guide")
public class TrackingResponseDto {

    @JsonProperty("guide_number")
    @Schema(description = "Unique tracking number", example = "202500000001")
    private String guideNumber;

    @JsonProperty("current_status")
    @Schema(description = "Current delivery status", example = "En Ruta")
    private String currentStatus;

    @JsonProperty("recipient_name")
    @Schema(description = "Name of the recipient", example = "Juan Pérez")
    private String recipientName;

    @JsonProperty("recipient_address")
    @Schema(description = "Delivery address", example = "Avenida Reforma 10-60 Zona 10")
    private String recipientAddress;

    @JsonProperty("recipient_city")
    @Schema(description = "Delivery city", example = "Guatemala")
    private String recipientCity;

    @JsonProperty("recipient_state")
    @Schema(description = "Delivery state", example = "Guatemala")
    private String recipientState;

    @JsonProperty("base_price")
    @Schema(description = "Base delivery price", example = "25.00")
    private BigDecimal basePrice;

    @JsonProperty("created_at")
    @Schema(description = "Guide creation date")
    private LocalDateTime createdAt;

    @JsonProperty("assignment_date")
    @Schema(description = "Assignment date to courier")
    private LocalDateTime assignmentDate;

    @JsonProperty("pickup_date")
    @Schema(description = "Package pickup date")
    private LocalDateTime pickupDate;

    @JsonProperty("delivery_date")
    @Schema(description = "Expected or actual delivery date")
    private LocalDateTime deliveryDate;

    @JsonProperty("observations")
    @Schema(description = "Additional observations", example = "Fragile package")
    private String observations;

    @JsonProperty("can_reject")
    @Schema(description = "Whether the delivery can be rejected by the recipient", example = "true")
    private Boolean canReject;

    @JsonProperty("business_name")
    @Schema(description = "Name of the sending business", example = "Tienda Electrónica")
    private String businessName;

    @JsonProperty("courier_name")
    @Schema(description = "Name of assigned courier", example = "Pedro González")
    private String courierName;

    @JsonProperty("courier_phone")
    @Schema(description = "Phone of assigned courier", example = "56123456")
    private String courierPhone;

    @JsonProperty("status_history")
    @Schema(description = "History of status changes")
    private List<TrackingHistoryDto> statusHistory;
}
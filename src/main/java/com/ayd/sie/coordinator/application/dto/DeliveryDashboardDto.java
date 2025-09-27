package com.ayd.sie.coordinator.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Delivery dashboard data transfer object")
public class DeliveryDashboardDto {

    @Schema(description = "Dashboard date", example = "2024-09-27")
    @JsonProperty("dashboard_date")
    private LocalDate dashboardDate;

    @Schema(description = "Last update timestamp", example = "2024-09-27T14:30:00")
    @JsonProperty("last_updated")
    private LocalDateTime lastUpdated;

    @Schema(description = "Total deliveries created today", example = "25")
    @JsonProperty("total_created")
    private Long totalCreated;

    @Schema(description = "Total deliveries assigned", example = "20")
    @JsonProperty("total_assigned")
    private Long totalAssigned;

    @Schema(description = "Total deliveries picked up", example = "18")
    @JsonProperty("total_picked_up")
    private Long totalPickedUp;

    @Schema(description = "Total deliveries in route", example = "12")
    @JsonProperty("total_in_route")
    private Long totalInRoute;

    @Schema(description = "Total deliveries completed", example = "15")
    @JsonProperty("total_completed")
    private Long totalCompleted;

    @Schema(description = "Total deliveries cancelled", example = "3")
    @JsonProperty("total_cancelled")
    private Long totalCancelled;

    @Schema(description = "Total deliveries rejected", example = "1")
    @JsonProperty("total_rejected")
    private Long totalRejected;

    @Schema(description = "Total deliveries with incidents", example = "2")
    @JsonProperty("total_incidents")
    private Long totalIncidents;

    @Schema(description = "Pending assignments (created but not assigned)", example = "5")
    @JsonProperty("pending_assignments")
    private Long pendingAssignments;

    @Schema(description = "Active couriers count", example = "8")
    @JsonProperty("active_couriers")
    private Long activeCouriers;

    @Schema(description = "Couriers with active contracts", example = "8")
    @JsonProperty("couriers_with_contracts")
    private Long couriersWithContracts;

    @Schema(description = "Unresolved incidents count", example = "1")
    @JsonProperty("unresolved_incidents")
    private Long unresolvedIncidents;

    @Schema(description = "Completion percentage", example = "75.0")
    @JsonProperty("completion_percentage")
    private Double completionPercentage;

    @Schema(description = "Efficiency metric (completed vs total)", example = "85.7")
    @JsonProperty("efficiency_metric")
    private Double efficiencyMetric;

    @Schema(description = "Recent pending deliveries")
    @JsonProperty("recent_pending_deliveries")
    private List<DeliveryStatusDto> recentPendingDeliveries;

    @Schema(description = "Recent incidents")
    @JsonProperty("recent_incidents")
    private List<IncidentSummaryDto> recentIncidents;

    @Schema(description = "Courier workload distribution")
    @JsonProperty("courier_workload")
    private List<CourierWorkloadDto> courierWorkload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Delivery status summary")
    public static class DeliveryStatusDto {
        @JsonProperty("guide_id")
        private Integer guideId;

        @JsonProperty("guide_number")
        private String guideNumber;

        @JsonProperty("business_name")
        private String businessName;

        @JsonProperty("recipient_name")
        private String recipientName;

        @JsonProperty("current_state")
        private String currentState;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        @JsonProperty("assigned_courier")
        private String assignedCourier;

        @JsonProperty("priority")
        private String priority;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Incident summary")
    public static class IncidentSummaryDto {
        @JsonProperty("incident_id")
        private Integer incidentId;

        @JsonProperty("guide_number")
        private String guideNumber;

        @JsonProperty("incident_type")
        private String incidentType;

        @JsonProperty("reported_by")
        private String reportedBy;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        @JsonProperty("resolved")
        private Boolean resolved;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Courier workload summary")
    public static class CourierWorkloadDto {
        @JsonProperty("courier_id")
        private Integer courierId;

        @JsonProperty("courier_name")
        private String courierName;

        @JsonProperty("assigned_count")
        private Long assignedCount;

        @JsonProperty("completed_count")
        private Long completedCount;

        @JsonProperty("pending_count")
        private Long pendingCount;

        @JsonProperty("incidents_count")
        private Long incidentsCount;

        @JsonProperty("has_active_contract")
        private Boolean hasActiveContract;

        @JsonProperty("completion_rate")
        private Double completionRate;
    }
}
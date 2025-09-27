package com.ayd.sie.admin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User references in the system")
public class UserReferencesDto {

    @JsonProperty("user_id")
    @Schema(description = "User ID")
    private Integer userId;

    @JsonProperty("has_references")
    @Schema(description = "Indicates if user has references in the system")
    private boolean hasReferences;

    @JsonProperty("tracking_guides_as_courier")
    @Schema(description = "Number of tracking guides assigned as courier")
    private long trackingGuidesAsCourier;

    @JsonProperty("active_guides_as_courier")
    @Schema(description = "Number of active tracking guides as courier")
    private long activeGuidesAsCourier;

    @JsonProperty("tracking_guides_as_coordinator")
    @Schema(description = "Number of tracking guides as coordinator")
    private long trackingGuidesAsCoordinator;

    @JsonProperty("active_guides_as_coordinator")
    @Schema(description = "Number of active tracking guides as coordinator")
    private long activeGuidesAsCoordinator;

    @JsonProperty("state_history_entries")
    @Schema(description = "Number of state history entries")
    private long stateHistoryEntries;

    @JsonProperty("reported_incidents")
    @Schema(description = "Number of reported incidents")
    private long reportedIncidents;

    @JsonProperty("resolved_incidents")
    @Schema(description = "Number of resolved incidents")
    private long resolvedIncidents;

    @JsonProperty("cancellations")
    @Schema(description = "Number of cancellations made")
    private long cancellations;

    @JsonProperty("courier_settlements")
    @Schema(description = "Number of courier settlements")
    private long courierSettlements;

    @JsonProperty("pending_settlements")
    @Schema(description = "Number of pending settlements")
    private long pendingSettlements;

    @JsonProperty("has_business")
    @Schema(description = "Indicates if user has an associated business")
    private boolean hasBusiness;

    @JsonProperty("business_name")
    @Schema(description = "Business name if exists")
    private String businessName;

    @JsonProperty("audit_log_entries")
    @Schema(description = "Number of audit log entries")
    private long auditLogEntries;
}
package com.ayd.sie.coordinator.infrastructure.web;

import com.ayd.sie.coordinator.application.dto.*;
import com.ayd.sie.coordinator.application.services.CoordinatorApplicationService;
import com.ayd.sie.shared.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coordinator")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Coordinator", description = "Coordinator operations for delivery management")
@SecurityRequirement(name = "bearerAuth")
public class CoordinatorController {

    private final CoordinatorApplicationService coordinatorApplicationService;

    // ===== DELIVERY ASSIGNMENT ENDPOINTS =====

    @PostMapping("/assignments")
    @Operation(summary = "Assign delivery to courier", description = "Assigns a delivery guide to an available courier")
    @ApiResponse(responseCode = "200", description = "Delivery assigned successfully")
    @ApiResponse(responseCode = "400", description = "Invalid assignment request")
    @ApiResponse(responseCode = "404", description = "Guide or courier not found")
    public ResponseEntity<AssignmentDto> assignDelivery(@Valid @RequestBody AssignDeliveryRequestDto request) {

        Integer coordinatorId = SecurityUtils.getCurrentUserId();
        AssignmentDto assignment = coordinatorApplicationService.assignDelivery(request, coordinatorId);

        log.info("Delivery assigned by coordinator {} - Guide: {}, Courier: {}",
                coordinatorId, request.getGuideId(), request.getCourierId());

        return ResponseEntity.ok(assignment);
    }

    @GetMapping("/deliveries/pending")
    @Operation(summary = "Get pending deliveries", description = "Retrieves paginated list of pending deliveries that need assignment")
    @ApiResponse(responseCode = "200", description = "Pending deliveries retrieved successfully")
    public ResponseEntity<Page<AssignmentDto>> getPendingDeliveries(
            @Parameter(description = "Search term for filtering") @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        Page<AssignmentDto> pendingDeliveries = coordinatorApplicationService.getPendingDeliveries(search, pageable);
        return ResponseEntity.ok(pendingDeliveries);
    }

    @GetMapping("/couriers/available")
    @Operation(summary = "Get available couriers", description = "Retrieves all active couriers with their workload and contract status")
    @ApiResponse(responseCode = "200", description = "Available couriers retrieved successfully")
    public ResponseEntity<List<DeliveryDashboardDto.CourierWorkloadDto>> getAvailableCouriers() {

        List<DeliveryDashboardDto.CourierWorkloadDto> couriers = coordinatorApplicationService.getAvailableCouriers();
        return ResponseEntity.ok(couriers);
    }

    @PutMapping("/assignments/{guideId}/reassign")
    @Operation(summary = "Reassign delivery", description = "Reassigns a delivery to a different courier")
    @ApiResponse(responseCode = "200", description = "Delivery reassigned successfully")
    @ApiResponse(responseCode = "400", description = "Invalid reassignment request")
    @ApiResponse(responseCode = "404", description = "Guide or courier not found")
    public ResponseEntity<AssignmentDto> reassignDelivery(
            @Parameter(description = "Guide ID to reassign") @PathVariable Integer guideId,
            @Parameter(description = "New courier ID") @RequestParam Integer newCourierId,
            @Parameter(description = "Reason for reassignment") @RequestParam String reason) {

        Integer coordinatorId = SecurityUtils.getCurrentUserId();
        AssignmentDto assignment = coordinatorApplicationService.reassignDelivery(guideId, newCourierId, reason,
                coordinatorId);

        log.info("Delivery reassigned by coordinator {} - Guide: {}, New courier: {}",
                coordinatorId, guideId, newCourierId);

        return ResponseEntity.ok(assignment);
    }

    // ===== INCIDENT MANAGEMENT ENDPOINTS =====

    @PostMapping("/incidents")
    @Operation(summary = "Report delivery incident", description = "Reports an incident for a delivery in progress")
    @ApiResponse(responseCode = "200", description = "Incident reported successfully")
    @ApiResponse(responseCode = "400", description = "Invalid incident data")
    @ApiResponse(responseCode = "404", description = "Guide not found")
    public ResponseEntity<IncidentDto> reportIncident(@Valid @RequestBody ReportIncidentRequestDto request) {

        Integer coordinatorId = SecurityUtils.getCurrentUserId();
        IncidentDto incident = coordinatorApplicationService.reportIncident(request, coordinatorId);

        log.info("Incident reported by coordinator {} for guide {}", coordinatorId, request.getGuideId());

        return ResponseEntity.ok(incident);
    }

    @PutMapping("/incidents/{incidentId}/resolve")
    @Operation(summary = "Resolve incident", description = "Marks an incident as resolved with resolution details")
    @ApiResponse(responseCode = "200", description = "Incident resolved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid resolution data")
    @ApiResponse(responseCode = "404", description = "Incident not found")
    public ResponseEntity<IncidentDto> resolveIncident(
            @Parameter(description = "Incident ID to resolve") @PathVariable Integer incidentId,
            @Valid @RequestBody ResolveIncidentRequestDto request) {

        Integer coordinatorId = SecurityUtils.getCurrentUserId();
        IncidentDto incident = coordinatorApplicationService.resolveIncident(incidentId, request, coordinatorId);

        log.info("Incident {} resolved by coordinator {}", incidentId, coordinatorId);

        return ResponseEntity.ok(incident);
    }

    @GetMapping("/incidents")
    @Operation(summary = "Get delivery incidents", description = "Retrieves paginated list of delivery incidents with optional filtering")
    @ApiResponse(responseCode = "200", description = "Incidents retrieved successfully")
    public ResponseEntity<Page<IncidentDto>> getIncidents(
            @Parameter(description = "Filter by resolution status") @RequestParam(required = false) Boolean resolved,
            @Parameter(description = "Search term for filtering") @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        Page<IncidentDto> incidents = coordinatorApplicationService.getIncidents(resolved, search, pageable);
        return ResponseEntity.ok(incidents);
    }

    // ===== CANCELLATION ENDPOINTS =====

    @PostMapping("/cancellations")
    @Operation(summary = "Process delivery cancellation", description = "Processes a delivery cancellation with penalty calculation")
    @ApiResponse(responseCode = "200", description = "Cancellation processed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid cancellation request")
    @ApiResponse(responseCode = "404", description = "Guide not found")
    public ResponseEntity<CancellationDto> processCancellation(
            @Valid @RequestBody ProcessCancellationRequestDto request) {

        Integer coordinatorId = SecurityUtils.getCurrentUserId();
        CancellationDto cancellation = coordinatorApplicationService.processCancellation(request, coordinatorId);

        log.info("Cancellation processed by coordinator {} for guide {}", coordinatorId, request.getGuideId());

        return ResponseEntity.ok(cancellation);
    }

    @GetMapping("/cancellations/{guideId}/validate")
    @Operation(summary = "Validate cancellation", description = "Validates if a delivery can be cancelled")
    @ApiResponse(responseCode = "200", description = "Validation result returned")
    public ResponseEntity<Map<String, Object>> validateCancellation(
            @Parameter(description = "Guide ID to validate") @PathVariable Integer guideId) {

        boolean canCancel = coordinatorApplicationService.validateCancellation(guideId);

        return ResponseEntity.ok(Map.of(
                "can_cancel", canCancel,
                "guide_id", guideId,
                "message", canCancel ? "Guide can be cancelled" : "Guide cannot be cancelled"));
    }

    // ===== DASHBOARD AND MONITORING ENDPOINTS =====

    @GetMapping("/dashboard")
    @Operation(summary = "Get delivery dashboard", description = "Retrieves comprehensive delivery dashboard data for a specific date")
    @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully")
    public ResponseEntity<DeliveryDashboardDto> getDeliveryDashboard(
            @Parameter(description = "Dashboard date (defaults to today)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (date == null) {
            date = LocalDate.now();
        }

        DeliveryDashboardDto dashboard = coordinatorApplicationService.getDeliveryDashboard(date);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/deliveries/history")
    @Operation(summary = "Get delivery history", description = "Get paginated delivery history with filters")
    @ApiResponse(responseCode = "200", description = "History retrieved successfully")
    public ResponseEntity<Page<AssignmentDto>> getDeliveryHistory(
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Search term") @RequestParam(required = false) String search,
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "assignmentDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDirection) {

        // Map frontend field names to entity field names
        String actualSortField = switch (sortBy) {
            case "assignedAt" -> "assignmentDate"; // Map frontend name to actual field
            case "guideNumber" -> "guideNumber";
            case "businessName" -> "business.businessName";
            case "recipientName" -> "recipientName";
            case "state" -> "currentState.stateName";
            default -> "assignmentDate";
        };

        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, actualSortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AssignmentDto> history = coordinatorApplicationService.getDeliveryHistory(
                status, search, startDate, endDate, pageable);

        return ResponseEntity.ok(history);
    }

    @GetMapping("/couriers/workload")
    @Operation(summary = "Get courier workload", description = "Retrieves detailed workload information for all couriers")
    @ApiResponse(responseCode = "200", description = "Courier workload retrieved successfully")
    public ResponseEntity<List<DeliveryDashboardDto.CourierWorkloadDto>> getCourierWorkload() {

        List<DeliveryDashboardDto.CourierWorkloadDto> workload = coordinatorApplicationService.getCourierWorkload();
        return ResponseEntity.ok(workload);
    }

    // ===== RESCHEDULE ENDPOINTS =====

    @PutMapping("/deliveries/{guideId}/reschedule")
    @Operation(summary = "Reschedule delivery", description = "Reschedules a delivery with optional courier change")
    @ApiResponse(responseCode = "200", description = "Delivery rescheduled successfully")
    @ApiResponse(responseCode = "400", description = "Invalid reschedule request")
    @ApiResponse(responseCode = "404", description = "Guide not found")
    public ResponseEntity<RescheduleDto> rescheduleDelivery(
            @Parameter(description = "Guide ID to reschedule") @PathVariable Integer guideId,
            @Valid @RequestBody RescheduleDto request) {

        Integer coordinatorId = SecurityUtils.getCurrentUserId();
        RescheduleDto reschedule = coordinatorApplicationService.rescheduleDelivery(guideId, request, coordinatorId);

        log.info("Delivery rescheduled by coordinator {} - Guide: {}", coordinatorId, guideId);

        return ResponseEntity.ok(reschedule);
    }

    // ===== UTILITY ENDPOINTS =====

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Checks coordinator module health")
    @ApiResponse(responseCode = "200", description = "Module is healthy")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "module", "coordinator",
                "timestamp", java.time.LocalDateTime.now()));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get coordinator statistics", description = "Retrieves basic statistics for coordinator operations")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getStatistics() {

        Integer coordinatorId = SecurityUtils.getCurrentUserId();

        // Get basic metrics
        Page<AssignmentDto> pendingPage = coordinatorApplicationService.getPendingDeliveries(null, Pageable.ofSize(1));
        Page<IncidentDto> incidentsPage = coordinatorApplicationService.getIncidents(false, null, Pageable.ofSize(1));
        List<DeliveryDashboardDto.CourierWorkloadDto> couriers = coordinatorApplicationService.getAvailableCouriers();

        long activeCouriers = couriers.stream()
                .filter(DeliveryDashboardDto.CourierWorkloadDto::getHasActiveContract)
                .count();

        return ResponseEntity.ok(Map.of(
                "coordinator_id", coordinatorId,
                "pending_deliveries", pendingPage.getTotalElements(),
                "unresolved_incidents", incidentsPage.getTotalElements(),
                "active_couriers", activeCouriers,
                "total_couriers", couriers.size()));
    }

    // ===== GET ALL DATA ENDPOINTS (FOR COORDINATORS) =====

    @GetMapping("/deliveries/all")
    @Operation(summary = "Get all deliveries", description = "Retrieves paginated list of all deliveries in the system")
    @ApiResponse(responseCode = "200", description = "All deliveries retrieved successfully")
    public ResponseEntity<Page<AssignmentDto>> getAllDeliveries(
            @PageableDefault(size = 20, sort = "assignmentDate") Pageable pageable) {

        Page<AssignmentDto> allDeliveries = coordinatorApplicationService.getAllDeliveries(pageable);
        return ResponseEntity.ok(allDeliveries);
    }

    @GetMapping("/commissions/all")
    @Operation(summary = "Get all commissions", description = "Retrieves paginated list of all commissions in the system")
    @ApiResponse(responseCode = "200", description = "All commissions retrieved successfully")
    public ResponseEntity<Page<AllCommissionsDto>> getAllCommissions(
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20, sort = "deliveryDate") Pageable pageable) {

        Page<AllCommissionsDto> allCommissions = coordinatorApplicationService.getAllCommissions(startDate, endDate,
                pageable);
        return ResponseEntity.ok(allCommissions);
    }
}
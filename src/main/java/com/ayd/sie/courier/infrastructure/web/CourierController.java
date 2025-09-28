package com.ayd.sie.courier.infrastructure.web;

import com.ayd.sie.courier.application.CourierApplicationService;
import com.ayd.sie.courier.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courier")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Courier", description = "Courier delivery management operations")
@SecurityRequirement(name = "bearerAuth")
public class CourierController {

    private final CourierApplicationService courierApplicationService;

    @PostMapping("/assignments/accept")
    @Operation(summary = "Accept delivery assignment", description = "Allows a courier to accept a delivery assignment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Assignment accepted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - not a courier or assignment not available"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Guide not found")
    })
    public ResponseEntity<AcceptAssignmentDto> acceptAssignment(
            @Valid @RequestBody AcceptAssignmentDto request,
            Authentication authentication) {

        Integer courierId = getCurrentUserId(authentication);
        AcceptAssignmentDto result = courierApplicationService.acceptAssignment(request, courierId);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/deliveries/state")
    @Operation(summary = "Update delivery state", description = "Updates the state of a delivery (Recogida, En Ruta, Entregada, Incidencia)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "State updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid state transition or request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - not assigned to this courier"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Guide not found")
    })
    public ResponseEntity<CourierDeliveryDto> updateDeliveryState(
            @Valid @RequestBody UpdateStateDto request,
            Authentication authentication) {

        Integer courierId = getCurrentUserId(authentication);
        CourierDeliveryDto result = courierApplicationService.updateDeliveryState(request, courierId);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/evidence")
    @Operation(summary = "Register delivery evidence", description = "Registers evidence (photo, signature, note) for a delivery")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evidence registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid evidence data or state"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - not assigned to this courier"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Guide not found")
    })
    public ResponseEntity<EvidenceDto> registerEvidence(
            @Valid @RequestBody EvidenceDto request,
            Authentication authentication) {

        Integer courierId = getCurrentUserId(authentication);
        EvidenceDto result = courierApplicationService.registerEvidence(request, courierId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/deliveries/{guideId}/evidence")
    @Operation(summary = "Get delivery evidence", description = "Retrieves all evidence for a specific delivery")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evidence retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - not assigned to this courier"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Guide not found")
    })
    public ResponseEntity<List<EvidenceDto>> getGuideEvidence(
            @Parameter(description = "Guide ID") @PathVariable Integer guideId,
            Authentication authentication) {

        Integer courierId = getCurrentUserId(authentication);
        List<EvidenceDto> result = courierApplicationService.getGuideEvidence(guideId, courierId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/deliveries")
    @Operation(summary = "Get courier deliveries", description = "Retrieves paginated list of deliveries assigned to the courier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Deliveries retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - not a courier")
    })
    public ResponseEntity<Page<CourierDeliveryDto>> getCourierDeliveries(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {

        Integer courierId = getCurrentUserId(authentication);
        Page<CourierDeliveryDto> result = courierApplicationService.getCourierDeliveries(
                courierId, page, size, sortBy, sortDir);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/deliveries/active")
    @Operation(summary = "Get active deliveries", description = "Retrieves all active deliveries for the courier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active deliveries retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - not a courier")
    })
    public ResponseEntity<List<CourierDeliveryDto>> getActiveDeliveries(
            Authentication authentication) {

        Integer courierId = getCurrentUserId(authentication);
        List<CourierDeliveryDto> result = courierApplicationService.getActiveDeliveries(courierId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/deliveries/state/{stateName}")
    @Operation(summary = "Get deliveries by state", description = "Retrieves deliveries in a specific state")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Deliveries retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid state name"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - not a courier")
    })
    public ResponseEntity<List<CourierDeliveryDto>> getDeliveriesByState(
            @Parameter(description = "State name") @PathVariable String stateName,
            Authentication authentication) {

        Integer courierId = getCurrentUserId(authentication);
        List<CourierDeliveryDto> result = courierApplicationService.getDeliveriesByState(courierId, stateName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/deliveries/{guideId}")
    @Operation(summary = "Get delivery detail", description = "Retrieves detailed information for a specific delivery")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Delivery detail retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - not assigned to this courier"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Guide not found")
    })
    public ResponseEntity<CourierDeliveryDto> getDeliveryDetail(
            @Parameter(description = "Guide ID") @PathVariable Integer guideId,
            Authentication authentication) {

        Integer courierId = getCurrentUserId(authentication);
        CourierDeliveryDto result = courierApplicationService.getDeliveryDetail(guideId, courierId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/commissions")
    @Operation(summary = "Get commission history", description = "Retrieves commission history for a date range")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Commission history retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - not a courier")
    })
    public ResponseEntity<CommissionDto> getCommissionHistory(
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {

        Integer courierId = getCurrentUserId(authentication);
        CommissionDto result = courierApplicationService.getCommissionHistory(courierId, startDate, endDate);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/commissions/total")
    @Operation(summary = "Get total commissions", description = "Calculates total commissions for a date range")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total commissions calculated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - not a courier")
    })
    public ResponseEntity<BigDecimal> getTotalCommissions(
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {

        Integer courierId = getCurrentUserId(authentication);
        BigDecimal result = courierApplicationService.getTotalCommissions(courierId, startDate, endDate);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/commissions/monthly")
    @Operation(summary = "Get monthly commissions", description = "Calculates commissions for a specific month")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Monthly commissions calculated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - not a courier")
    })
    public ResponseEntity<BigDecimal> getMonthlyCommissions(
            @Parameter(description = "Year") @RequestParam int year,
            @Parameter(description = "Month (1-12)") @RequestParam int month,
            Authentication authentication) {

        Integer courierId = getCurrentUserId(authentication);
        BigDecimal result = courierApplicationService.getMonthlyCommissions(courierId, year, month);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/incidents")
    @Operation(summary = "Report incident", description = "Reports a delivery incident")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Incident reported successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid incident data or delivery state"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - not assigned to this courier"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Guide not found")
    })
    public ResponseEntity<ReportIncidentDto> reportIncident(
            @Valid @RequestBody ReportIncidentDto request,
            Authentication authentication) {

        Integer courierId = getCurrentUserId(authentication);
        ReportIncidentDto result = courierApplicationService.reportIncident(request, courierId);

        return ResponseEntity.ok(result);
    }

    private Integer getCurrentUserId(Authentication authentication) {
        return Integer.parseInt(authentication.getName());
    }
}
package com.ayd.sie.coordinator.application.services;

import com.ayd.sie.coordinator.application.dto.*;
import com.ayd.sie.coordinator.application.usecases.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoordinatorApplicationService {

    // Delivery assignment use cases
    private final AssignDeliveryUseCase assignDeliveryUseCase;
    private final GetPendingDeliveriesUseCase getPendingDeliveriesUseCase;
    private final GetAvailableCouriersUseCase getAvailableCouriersUseCase;
    private final ReassignDeliveryUseCase reassignDeliveryUseCase;

    // Incident management use cases
    private final HandleIncidentUseCase handleIncidentUseCase;
    private final ResolveIncidentUseCase resolveIncidentUseCase;
    private final GetIncidentsUseCase getIncidentsUseCase;
    private final GetIncidentTypesUseCase getIncidentTypesUseCase;
    private final GetCancellationTypesUseCase getCancellationTypesUseCase;

    // Cancellation processing use cases
    private final ProcessCancellationUseCase processCancellationUseCase;
    private final ValidateCancellationUseCase validateCancellationUseCase;

    // Monitoring and dashboard use cases
    private final GetDeliveryDashboardUseCase getDeliveryDashboardUseCase;
    private final GetDeliveryHistoryUseCase getDeliveryHistoryUseCase;
    private final GetCourierWorkloadUseCase getCourierWorkloadUseCase;

    // Reschedule use cases
    private final RescheduleDeliveryUseCase rescheduleDeliveryUseCase;

    // Get all data use cases (for coordinators)
    private final GetAllDeliveriesUseCase getAllDeliveriesUseCase;
    private final GetAllCommissionsUseCase getAllCommissionsUseCase;

    // === DELIVERY ASSIGNMENT OPERATIONS ===

    public AssignmentDto assignDelivery(AssignDeliveryRequestDto request, Integer coordinatorId) {
        return assignDeliveryUseCase.execute(request, coordinatorId);
    }

    public Page<AssignmentDto> getPendingDeliveries(String search, Pageable pageable) {
        return getPendingDeliveriesUseCase.execute(search, pageable);
    }

    public List<DeliveryDashboardDto.CourierWorkloadDto> getAvailableCouriers() {
        return getAvailableCouriersUseCase.execute();
    }

    public AssignmentDto reassignDelivery(Integer guideId, Integer newCourierId, String reason, Integer coordinatorId) {
        return reassignDeliveryUseCase.execute(guideId, newCourierId, reason, coordinatorId);
    }

    // === INCIDENT MANAGEMENT OPERATIONS ===

    public IncidentDto reportIncident(ReportIncidentRequestDto request, Integer coordinatorId) {
        return handleIncidentUseCase.execute(request, coordinatorId);
    }

    public IncidentDto resolveIncident(Integer incidentId, ResolveIncidentRequestDto request, Integer coordinatorId) {
        return resolveIncidentUseCase.execute(incidentId, request, coordinatorId);
    }

    public Page<IncidentDto> getIncidents(Boolean resolved, String search, Pageable pageable) {
        return getIncidentsUseCase.execute(resolved, search, pageable);
    }

    public List<IncidentTypeDto> getIncidentTypes() {
        return getIncidentTypesUseCase.execute();
    }

    public List<CancellationTypeDto> getCancellationTypes() {
        return getCancellationTypesUseCase.execute();
    }

    // === CANCELLATION PROCESSING OPERATIONS ===

    public CancellationDto processCancellation(ProcessCancellationRequestDto request, Integer coordinatorId) {
        return processCancellationUseCase.execute(request, coordinatorId);
    }

    public boolean validateCancellation(Integer guideId) {
        return validateCancellationUseCase.execute(guideId);
    }

    // === MONITORING AND DASHBOARD OPERATIONS ===

    public DeliveryDashboardDto getDeliveryDashboard(LocalDate date) {
        return getDeliveryDashboardUseCase.execute(date);
    }

    public Page<AssignmentDto> getDeliveryHistory(String status, String search, LocalDate startDate, LocalDate endDate,
            Pageable pageable) {
        return getDeliveryHistoryUseCase.execute(status, search, startDate, endDate, pageable);
    }

    public List<DeliveryDashboardDto.CourierWorkloadDto> getCourierWorkload() {
        return getCourierWorkloadUseCase.execute();
    }

    // === RESCHEDULE OPERATIONS ===

    public RescheduleDto rescheduleDelivery(Integer guideId, RescheduleDto request, Integer coordinatorId) {
        return rescheduleDeliveryUseCase.execute(guideId, request, coordinatorId);
    }

    // === GET ALL DATA OPERATIONS (FOR COORDINATORS) ===

    public Page<AssignmentDto> getAllDeliveries(Pageable pageable) {
        return getAllDeliveriesUseCase.execute(pageable);
    }

    public Page<AllCommissionsDto> getAllCommissions(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return getAllCommissionsUseCase.execute(startDate, endDate, pageable);
    }
}

package com.ayd.sie.courier.application;

import com.ayd.sie.courier.application.dto.*;
import com.ayd.sie.courier.application.usecases.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourierApplicationService {

    private final AcceptAssignmentUseCase acceptAssignmentUseCase;
    private final UpdateDeliveryStateUseCase updateDeliveryStateUseCase;
    private final RegisterEvidenceUseCase registerEvidenceUseCase;
    private final GetCourierDeliveriesUseCase getCourierDeliveriesUseCase;
    private final GetCommissionHistoryUseCase getCommissionHistoryUseCase;
    private final ReportIncidentUseCase reportIncidentUseCase;

    // Assignment Management
    public AcceptAssignmentDto acceptAssignment(AcceptAssignmentDto request, Integer courierId) {
        log.info("Processing assignment acceptance for courier {} and guide {}", courierId, request.getGuideId());
        return acceptAssignmentUseCase.execute(request, courierId);
    }

    // State Management
    public CourierDeliveryDto updateDeliveryState(UpdateStateDto request, Integer courierId) {
        log.info("Processing state update for courier {} and guide {}", courierId, request.getGuideId());
        return updateDeliveryStateUseCase.execute(request, courierId);
    }

    // Evidence Management
    public EvidenceDto registerEvidence(EvidenceDto request, Integer courierId) {
        log.info("Processing evidence registration for courier {} and guide {}", courierId, request.getGuideId());
        return registerEvidenceUseCase.execute(request, courierId);
    }

    public List<EvidenceDto> getGuideEvidence(Integer guideId, Integer courierId) {
        log.info("Retrieving evidence for guide {} by courier {}", guideId, courierId);
        return registerEvidenceUseCase.getGuideEvidence(guideId, courierId);
    }

    // Delivery Management
    public Page<CourierDeliveryDto> getCourierDeliveries(Integer courierId, int page, int size, String sortBy,
            String sortDir) {
        log.info("Retrieving deliveries for courier {} - page: {}, size: {}", courierId, page, size);
        return getCourierDeliveriesUseCase.execute(courierId, page, size, sortBy, sortDir);
    }

    public List<CourierDeliveryDto> getActiveDeliveries(Integer courierId) {
        log.info("Retrieving active deliveries for courier {}", courierId);
        return getCourierDeliveriesUseCase.getActiveDeliveries(courierId);
    }

    public List<CourierDeliveryDto> getDeliveriesByState(Integer courierId, String stateName) {
        log.info("Retrieving deliveries in state {} for courier {}", stateName, courierId);
        return getCourierDeliveriesUseCase.getDeliveriesByState(courierId, stateName);
    }

    public CourierDeliveryDto getDeliveryDetail(Integer guideId, Integer courierId) {
        log.info("Retrieving delivery detail for guide {} by courier {}", guideId, courierId);
        return getCourierDeliveriesUseCase.getDeliveryDetail(guideId, courierId);
    }

    // Commission Management
    public CommissionDto getCommissionHistory(Integer courierId, LocalDate startDate, LocalDate endDate) {
        log.info("Retrieving commission history for courier {} from {} to {}", courierId, startDate, endDate);
        return getCommissionHistoryUseCase.execute(courierId, startDate, endDate);
    }

    public BigDecimal getTotalCommissions(Integer courierId, LocalDate startDate, LocalDate endDate) {
        log.info("Calculating total commissions for courier {} from {} to {}", courierId, startDate, endDate);
        return getCommissionHistoryUseCase.getTotalCommissions(courierId, startDate, endDate);
    }

    public BigDecimal getMonthlyCommissions(Integer courierId, int year, int month) {
        log.info("Calculating monthly commissions for courier {} - {}/{}", courierId, month, year);
        return getCommissionHistoryUseCase.getMonthlyCommissions(courierId, year, month);
    }

    // Incident Management
    public ReportIncidentDto reportIncident(ReportIncidentDto request, Integer courierId) {
        log.info("Processing incident report for courier {} and guide {}", courierId, request.getGuideId());
        return reportIncidentUseCase.execute(request, courierId);
    }
}
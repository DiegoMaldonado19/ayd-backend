package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.IncidentDto;
import com.ayd.sie.coordinator.application.dto.ResolveIncidentRequestDto;
import com.ayd.sie.shared.domain.entities.*;
import com.ayd.sie.shared.domain.exceptions.BusinessConstraintViolationException;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.domain.services.NotificationService;
import com.ayd.sie.shared.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResolveIncidentUseCase {

    private final DeliveryIncidentJpaRepository deliveryIncidentRepository;
    private final UserJpaRepository userRepository;
    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final TrackingStateJpaRepository trackingStateRepository;
    private final StateHistoryJpaRepository stateHistoryRepository;
    private final NotificationService notificationService;

    @Transactional
    public IncidentDto execute(Integer incidentId, ResolveIncidentRequestDto request, Integer coordinatorId) {
        // 1. Validate coordinator
        User coordinator = userRepository.findById(coordinatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator not found"));

        if (!coordinator.getRole().getRoleName().equals("Coordinador")) {
            throw new BusinessConstraintViolationException("Only coordinators can resolve incidents");
        }

        // 2. Validate and get incident
        DeliveryIncident incident = deliveryIncidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

        if (Boolean.TRUE.equals(incident.getResolved())) {
            throw new BusinessConstraintViolationException("Incident is already resolved");
        }

        // 3. Get associated tracking guide
        TrackingGuide guide = incident.getGuide();

        // 4. Resolve the incident
        incident.setResolution(request.getResolution());
        incident.setResolved(true);
        incident.setResolvedAt(LocalDateTime.now());
        incident.setResolvedByUser(coordinator);
        incident.setUpdatedAt(LocalDateTime.now());

        DeliveryIncident savedIncident = deliveryIncidentRepository.save(incident);

        // 5. Handle reassignment if needed
        if (request.getNewCourierId() != null) {
            User newCourier = userRepository.findById(request.getNewCourierId())
                    .orElseThrow(() -> new ResourceNotFoundException("New courier not found"));

            if (!newCourier.getRole().getRoleName().equals("Repartidor")) {
                throw new BusinessConstraintViolationException("Can only reassign to couriers");
            }

            guide.setCourier(newCourier);
        }

        // 6. Update guide state based on resolution
        TrackingState newState = null;
        String stateChangeReason = "";

        if (request.getRescheduleDelivery()) {
            // If rescheduled, move back to assigned state
            newState = trackingStateRepository.findByStateName("Asignada")
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned state not found"));
            stateChangeReason = "Incident resolved - delivery rescheduled";
        } else {
            // If not rescheduled, determine appropriate state based on current situation
            String currentState = guide.getCurrentState().getStateName();
            if (currentState.equals("Incidencia")) {
                // Return to previous logical state (usually assigned or picked up)
                if (guide.getCourier() != null) {
                    newState = trackingStateRepository.findByStateName("Asignada")
                            .orElseThrow(() -> new ResourceNotFoundException("Assigned state not found"));
                    stateChangeReason = "Incident resolved - returned to assigned state";
                }
            }
        }

        // 7. Update guide if state change is needed
        if (newState != null) {
            guide.setCurrentState(newState);
            guide = trackingGuideRepository.save(guide);

            // Create state history record
            StateHistory stateHistory = StateHistory.builder()
                    .guide(guide)
                    .state(newState)
                    .user(coordinator)
                    .observations(stateChangeReason + " - " + request.getResolution())
                    .changedAt(LocalDateTime.now())
                    .build();

            stateHistoryRepository.save(stateHistory);
        }

        // 8. Send notifications
        try {
            // Notify business if requested
            if (request.getBusinessNotified() && guide.getBusiness() != null &&
                    guide.getBusiness().getUser() != null) {
                notificationService.sendBusinessNotification(
                        guide.getBusiness().getUser().getEmail(),
                        "Incidencia Resuelta",
                        String.format("La incidencia para su guía %s ha sido resuelta: %s",
                                guide.getGuideNumber(), request.getResolution()));
            }

            // Notify courier about resolution
            if (guide.getCourier() != null) {
                notificationService.sendCourierNotification(
                        guide.getCourier().getEmail(),
                        "Incidencia Resuelta",
                        String.format("La incidencia para la guía %s ha sido resuelta. %s",
                                guide.getGuideNumber(),
                                request.getRescheduleDelivery() ? "Puedes proceder con la entrega."
                                        : "Revisa las nuevas instrucciones."));
            }
        } catch (Exception e) {
            log.warn("Failed to send incident resolution notifications: {}", e.getMessage());
        }

        log.info("Incident resolved - ID: {}, Guide: {}, Coordinator: {}",
                savedIncident.getIncidentId(),
                guide.getGuideNumber(),
                coordinator.getEmail());

        // 9. Build and return response DTO
        return IncidentDto.builder()
                .incidentId(savedIncident.getIncidentId())
                .guideId(guide.getGuideId())
                .guideNumber(guide.getGuideNumber())
                .incidentTypeId(savedIncident.getIncidentTypeId())
                .reportedByUserId(savedIncident.getReportedByUser().getUserId())
                .reportedByName(savedIncident.getReportedByUser().getFirstName() + " " +
                        savedIncident.getReportedByUser().getLastName())
                .reportedByRole(savedIncident.getReportedByUser().getRole().getRoleName())
                .description(savedIncident.getDescription())
                .resolution(savedIncident.getResolution())
                .resolved(true)
                .resolvedAt(savedIncident.getResolvedAt())
                .resolvedByUserId(coordinator.getUserId())
                .resolvedByName(coordinator.getFirstName() + " " + coordinator.getLastName())
                .businessName(guide.getBusiness() != null ? guide.getBusiness().getBusinessName() : null)
                .recipientName(guide.getRecipientName())
                .recipientAddress(guide.getRecipientAddress())
                .currentState(guide.getCurrentState().getStateName())
                .createdAt(savedIncident.getCreatedAt())
                .updatedAt(savedIncident.getUpdatedAt())
                .build();
    }
}
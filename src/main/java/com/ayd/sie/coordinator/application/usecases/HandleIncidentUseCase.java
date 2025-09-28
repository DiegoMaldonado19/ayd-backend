package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.IncidentDto;
import com.ayd.sie.coordinator.application.dto.ReportIncidentRequestDto;
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
public class HandleIncidentUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final UserJpaRepository userRepository;
    private final DeliveryIncidentJpaRepository deliveryIncidentRepository;
    private final TrackingStateJpaRepository trackingStateRepository;
    private final StateHistoryJpaRepository stateHistoryRepository;
    private final IncidentTypeJpaRepository incidentTypeRepository;
    private final NotificationService notificationService;

    @Transactional
    public IncidentDto execute(ReportIncidentRequestDto request, Integer coordinatorId) {
        // 1. Validate coordinator
        User coordinator = userRepository.findById(coordinatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator not found"));

        if (!coordinator.getRole().getRoleName().equals("Coordinador")) {
            throw new BusinessConstraintViolationException("Only coordinators can report incidents");
        }

        // 2. Validate and get tracking guide
        TrackingGuide guide = trackingGuideRepository.findById(request.getGuideId())
                .orElseThrow(() -> new ResourceNotFoundException("Tracking guide not found"));

        // 3. Validate guide state - incidents can only be reported for guides in
        // transit
        String currentState = guide.getCurrentState().getStateName();
        if (!currentState.equals("Asignada") && !currentState.equals("Recogida") &&
                !currentState.equals("En Ruta") && !currentState.equals("Incidencia")) {
            throw new BusinessConstraintViolationException(
                    "Incidents can only be reported for deliveries in progress. Current state: " + currentState);
        }

        // 4. Validate incident type
        IncidentType incidentType = incidentTypeRepository.findById(request.getIncidentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Incident type not found"));

        // 5. Get incident state
        TrackingState incidentState = trackingStateRepository.findByStateName("Incidencia")
                .orElseThrow(() -> new ResourceNotFoundException("Incident state not found"));

        // 6. Create the incident record
        DeliveryIncident incident = DeliveryIncident.builder()
                .guide(guide)
                .incidentTypeId(request.getIncidentTypeId())
                .reportedByUser(coordinator)
                .description(request.getDescription())
                .resolved(false)
                .createdAt(LocalDateTime.now())
                .build();

        DeliveryIncident savedIncident = deliveryIncidentRepository.save(incident);

        // 7. Update guide state to "Incidencia"
        guide.setCurrentState(incidentState);
        TrackingGuide savedGuide = trackingGuideRepository.save(guide);

        // 8. Create state history record
        StateHistory stateHistory = StateHistory.builder()
                .guide(savedGuide)
                .state(incidentState)
                .user(coordinator)
                .observations("Incident reported: " + incidentType.getTypeName() + " - " + request.getDescription())
                .changedAt(LocalDateTime.now())
                .build();

        stateHistoryRepository.save(stateHistory);

        // 9. Send notifications
        try {
            // Notify business about incident
            if (savedGuide.getBusiness() != null && savedGuide.getBusiness().getUser() != null) {
                notificationService.sendBusinessNotification(
                        savedGuide.getBusiness().getUser().getEmail(),
                        "Incidencia Reportada",
                        String.format("Se ha reportado una incidencia para su guía %s: %s",
                                savedGuide.getGuideNumber(), incidentType.getTypeName()));
            }

            // Notify courier if assigned
            if (savedGuide.getCourier() != null) {
                notificationService.sendCourierNotification(
                        savedGuide.getCourier().getEmail(),
                        "Incidencia Reportada",
                        String.format("Se ha reportado una incidencia para la guía %s que tienes asignada",
                                savedGuide.getGuideNumber()));
            }
        } catch (Exception e) {
            log.warn("Failed to send incident notifications: {}", e.getMessage());
        }

        log.info("Incident reported - Guide: {}, Type: {}, Coordinator: {}",
                savedGuide.getGuideNumber(),
                incidentType.getTypeName(),
                coordinator.getEmail());

        // 10. Build and return response DTO
        return IncidentDto.builder()
                .incidentId(savedIncident.getIncidentId())
                .guideId(savedGuide.getGuideId())
                .guideNumber(savedGuide.getGuideNumber())
                .incidentTypeId(incidentType.getIncidentTypeId())
                .incidentTypeName(incidentType.getTypeName())
                .requiresReturn(incidentType.getRequiresReturn())
                .reportedByUserId(coordinator.getUserId())
                .reportedByName(coordinator.getFirstName() + " " + coordinator.getLastName())
                .reportedByRole(coordinator.getRole().getRoleName())
                .description(savedIncident.getDescription())
                .resolved(false)
                .businessName(savedGuide.getBusiness() != null ? savedGuide.getBusiness().getBusinessName() : null)
                .recipientName(savedGuide.getRecipientName())
                .recipientAddress(savedGuide.getRecipientAddress())
                .currentState(savedGuide.getCurrentState().getStateName())
                .createdAt(savedIncident.getCreatedAt())
                .build();
    }
}
package com.ayd.sie.courier.application.usecases;

import com.ayd.sie.courier.application.dto.ReportIncidentDto;
import com.ayd.sie.shared.domain.entities.DeliveryIncident;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.domain.entities.TrackingState;
import com.ayd.sie.shared.domain.entities.StateHistory;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.infrastructure.persistence.DeliveryIncidentJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingStateJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.StateHistoryJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import com.ayd.sie.shared.infrastructure.notifications.EmailService;
import com.ayd.sie.shared.domain.exceptions.BusinessConstraintViolationException;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportIncidentUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final UserJpaRepository userRepository;
    private final DeliveryIncidentJpaRepository deliveryIncidentRepository;
    private final TrackingStateJpaRepository trackingStateRepository;
    private final StateHistoryJpaRepository stateHistoryRepository;
    private final EmailService emailService;

    @Transactional
    public ReportIncidentDto execute(ReportIncidentDto request, Integer courierId) {
        log.info("Courier {} reporting incident for guide {}", courierId, request.getGuideId());

        // 1. Validate courier
        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        if (!courier.getRole().getRoleName().equals("Repartidor")) {
            throw new BusinessConstraintViolationException("Only couriers can report incidents");
        }

        // 2. Validate and get tracking guide
        TrackingGuide guide = trackingGuideRepository.findById(request.getGuideId())
                .orElseThrow(() -> new ResourceNotFoundException("Tracking guide not found"));

        // 3. Verify the guide is assigned to the requesting courier
        if (guide.getCourier() == null || !guide.getCourier().getUserId().equals(courierId)) {
            throw new BusinessConstraintViolationException("Guide is not assigned to this courier");
        }

        // 4. Validate guide state allows incident reporting
        String currentStateName = guide.getCurrentState().getStateName();
        if (!currentStateName.equals("Recogida") && !currentStateName.equals("En Ruta") &&
                !currentStateName.equals("Incidencia")) {
            throw new BusinessConstraintViolationException(
                    "Incidents can only be reported for deliveries in 'Recogida', 'En Ruta', or 'Incidencia' state. Current state: "
                            + currentStateName);
        }

        // 5. Create or update incident
        DeliveryIncident incident;
        if (request.getIncidentId() != null) {
            // Update existing incident
            incident = deliveryIncidentRepository.findById(request.getIncidentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

            // Verify ownership
            if (!incident.getGuide().getGuideId().equals(request.getGuideId())) {
                throw new BusinessConstraintViolationException("Incident does not belong to the specified guide");
            }

            updateIncident(incident, request);
        } else {
            // Create new incident
            incident = createIncident(guide, request, courier);
        }

        DeliveryIncident savedIncident = deliveryIncidentRepository.save(incident);

        // 6. Update guide state to "Incidencia" if it's a new incident
        if (request.getIncidentId() == null && !currentStateName.equals("Incidencia")) {
            updateGuideStateToIncident(guide);

            // 7. Send notification email to coordinator
            sendIncidentNotificationEmail(guide, savedIncident, courier);
        }

        log.info("Incident {} reported successfully for guide {} by courier {}",
                savedIncident.getIncidentId(), guide.getGuideId(), courierId);

        return mapToReportIncidentDto(savedIncident);
    }

    private void updateGuideStateToIncident(TrackingGuide guide) {
        // Get "Incidencia" state
        TrackingState incidentState = trackingStateRepository.findByStateName("Incidencia")
                .orElseThrow(() -> new ResourceNotFoundException("Incident state not found"));

        // Update guide state
        guide.setCurrentState(incidentState);
        trackingGuideRepository.save(guide);

        // Record state history
        StateHistory stateHistory = StateHistory.builder()
                .guide(guide)
                .state(incidentState)
                .user(guide.getCourier())
                .observations("Automatic state change due to incident reporting")
                .changedAt(LocalDateTime.now())
                .build();

        stateHistoryRepository.save(stateHistory);
    }

    private void sendIncidentNotificationEmail(TrackingGuide guide, DeliveryIncident incident, User courier) {
        try {
            String coordinatorEmail = guide.getCoordinator().getEmail();
            String subject = "INCIDENCIA REPORTADA - Guía: " + guide.getGuideNumber();

            StringBuilder content = new StringBuilder();
            content.append("<html><body>");
            content.append("<h2>Incidencia Reportada</h2>");
            content.append("<p><strong>Repartidor:</strong> ").append(courier.getFullName()).append("</p>");
            content.append("<p><strong>Número de Guía:</strong> ").append(guide.getGuideNumber()).append("</p>");
            content.append("<p><strong>Cliente:</strong> ").append(guide.getRecipientName()).append("</p>");
            content.append("<p><strong>Dirección:</strong> ").append(guide.getRecipientAddress()).append("</p>");
            content.append("<p><strong>Tipo de Incidencia:</strong> General</p>");
            content.append("<p><strong>Descripción:</strong> ").append(incident.getDescription()).append("</p>");
            content.append("<p><strong>Fecha de Reporte:</strong> ").append(incident.getCreatedAt().toString())
                    .append("</p>");
            content.append("<p>Se requiere atención inmediata para resolver esta incidencia.</p>");
            content.append("</body></html>");

            emailService.sendHtmlEmail(coordinatorEmail, subject, content.toString());

            log.info("Incident notification email sent to coordinator: {}", coordinatorEmail);
        } catch (Exception e) {
            log.error("Failed to send incident notification email for guide {}: {}",
                    guide.getGuideId(), e.getMessage());
            // Don't fail the transaction due to email issues
        }
    }

    private DeliveryIncident createIncident(TrackingGuide guide, ReportIncidentDto request, User reporter) {
        return DeliveryIncident.builder()
                .guide(guide)
                .incidentTypeId(1) // Default incident type ID, could be mapped from incident type string
                .description(request.getDescription())
                .reportedByUser(reporter)
                .resolved(false)
                .build();
    }

    private void updateIncident(DeliveryIncident incident, ReportIncidentDto request) {
        if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
            incident.setDescription(request.getDescription());
        }
        // Note: Latitude and longitude are not available in the current entity
        // structure
        // Incident type cannot be changed after creation
    }

    private ReportIncidentDto mapToReportIncidentDto(DeliveryIncident incident) {
        return ReportIncidentDto.builder()
                .incidentId(incident.getIncidentId())
                .guideId(incident.getGuide().getGuideId())
                .incidentType("General") // Default since we don't have incident type mapping
                .description(incident.getDescription())
                .latitude(null) // Not available in current entity
                .longitude(null) // Not available in current entity
                .reportedAt(incident.getCreatedAt())
                .resolved(incident.getResolved())
                .build();
    }
}
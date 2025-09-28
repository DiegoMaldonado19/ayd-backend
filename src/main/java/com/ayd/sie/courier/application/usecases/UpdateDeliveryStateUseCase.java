package com.ayd.sie.courier.application.usecases;

import com.ayd.sie.courier.application.dto.UpdateStateDto;
import com.ayd.sie.courier.application.dto.CourierDeliveryDto;
import com.ayd.sie.shared.domain.entities.*;
import com.ayd.sie.shared.infrastructure.persistence.*;
import com.ayd.sie.shared.domain.services.NotificationService;
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
public class UpdateDeliveryStateUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final UserJpaRepository userRepository;
    private final TrackingStateJpaRepository trackingStateRepository;
    private final StateHistoryJpaRepository stateHistoryRepository;
    private final DeliveryIncidentJpaRepository deliveryIncidentRepository;
    private final NotificationService notificationService;

    @Transactional
    public CourierDeliveryDto execute(UpdateStateDto request, Integer courierId) {
        log.info("Courier {} updating state to {} for guide {}", courierId, request.getNewState(),
                request.getGuideId());

        // 1. Validate courier
        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        if (!courier.getRole().getRoleName().equals("Repartidor")) {
            throw new BusinessConstraintViolationException("Only couriers can update delivery state");
        }

        // 2. Validate and get tracking guide
        TrackingGuide guide = trackingGuideRepository.findById(request.getGuideId())
                .orElseThrow(() -> new ResourceNotFoundException("Tracking guide not found"));

        // 3. Verify the guide is assigned to the requesting courier
        if (guide.getCourier() == null || !guide.getCourier().getUserId().equals(courierId)) {
            throw new BusinessConstraintViolationException("Guide is not assigned to this courier");
        }

        // 4. Validate current state allows updates
        String currentStateName = guide.getCurrentState().getStateName();
        if (currentStateName.equals("Entregada") || currentStateName.equals("Cancelada") ||
                currentStateName.equals("Rechazada")) {
            throw new BusinessConstraintViolationException(
                    "Cannot update state of completed deliveries. Current state: " + currentStateName);
        }

        // 5. Validate assignment is accepted for certain state changes
        if (!Boolean.TRUE.equals(guide.getAssignmentAccepted()) &&
                !request.getNewState().equals("Incidencia")) {
            throw new BusinessConstraintViolationException(
                    "Assignment must be accepted before updating to " + request.getNewState());
        }

        // 6. Validate state transition
        validateStateTransition(currentStateName, request.getNewState());

        // 7. Get new state
        TrackingState newState = trackingStateRepository.findByStateName(request.getNewState())
                .orElseThrow(() -> new ResourceNotFoundException("State not found: " + request.getNewState()));

        // 8. Update guide state and timestamps
        guide.setCurrentState(newState);
        LocalDateTime actionTime = request.getActionTimestamp() != null ? request.getActionTimestamp()
                : LocalDateTime.now();

        updateTimestamps(guide, request.getNewState(), actionTime);

        TrackingGuide savedGuide = trackingGuideRepository.save(guide);

        // 9. Record state history
        StateHistory stateHistory = StateHistory.builder()
                .guide(savedGuide)
                .state(newState)
                .user(courier)
                .observations(request.getObservations() != null ? request.getObservations()
                        : String.format("State changed to %s", request.getNewState()))
                .changedAt(actionTime)
                .build();

        stateHistoryRepository.save(stateHistory);

        // 10. Handle incidents if needed
        if (request.getNewState().equals("Incidencia")) {
            createIncident(savedGuide, request, courier);
        }

        // 11. Send notifications
        sendStateChangeNotifications(savedGuide, request.getNewState(), courier);

        log.info("State updated successfully to {} for guide {} by courier {}",
                request.getNewState(), guide.getGuideId(), courierId);

        return mapToDeliveryDto(savedGuide);
    }

    private void validateStateTransition(String currentState, String newState) {
        switch (currentState) {
            case "Asignada":
                if (!newState.equals("Recogida") && !newState.equals("Incidencia")) {
                    throw new BusinessConstraintViolationException(
                            "From 'Asignada' can only transition to 'Recogida' or 'Incidencia'");
                }
                break;
            case "Recogida":
                if (!newState.equals("En Ruta") && !newState.equals("Incidencia")) {
                    throw new BusinessConstraintViolationException(
                            "From 'Recogida' can only transition to 'En Ruta' or 'Incidencia'");
                }
                break;
            case "En Ruta":
                if (!newState.equals("Entregada") && !newState.equals("Incidencia")) {
                    throw new BusinessConstraintViolationException(
                            "From 'En Ruta' can only transition to 'Entregada' or 'Incidencia'");
                }
                break;
            case "Incidencia":
                if (!newState.equals("En Ruta") && !newState.equals("Entregada")) {
                    throw new BusinessConstraintViolationException(
                            "From 'Incidencia' can only transition to 'En Ruta' or 'Entregada'");
                }
                break;
            default:
                throw new BusinessConstraintViolationException(
                        "Invalid state transition from " + currentState + " to " + newState);
        }
    }

    private void updateTimestamps(TrackingGuide guide, String newState, LocalDateTime actionTime) {
        switch (newState) {
            case "Recogida":
                guide.setPickupDate(actionTime);
                break;
            case "Entregada":
                guide.setDeliveryDate(actionTime);
                break;
        }
    }

    private void createIncident(TrackingGuide guide, UpdateStateDto request, User courier) {
        if (request.getIncidentTypeId() == null || request.getIncidentDescription() == null) {
            throw new BusinessConstraintViolationException(
                    "Incident type ID and description are required when reporting incidents");
        }

        DeliveryIncident incident = DeliveryIncident.builder()
                .guide(guide)
                .incidentTypeId(request.getIncidentTypeId())
                .reportedByUser(courier)
                .description(request.getIncidentDescription())
                .resolved(false)
                .build();

        deliveryIncidentRepository.save(incident);
        log.info("Incident created for guide {} by courier {}", guide.getGuideId(), courier.getUserId());
    }

    private void sendStateChangeNotifications(TrackingGuide guide, String newState, User courier) {
        try {
            String message;
            switch (newState) {
                case "Recogida":
                    message = String.format("Your package %s has been picked up and is being processed",
                            guide.getGuideNumber());
                    break;
                case "En Ruta":
                    message = String.format("Your package %s is on the way to the destination",
                            guide.getGuideNumber());
                    break;
                case "Entregada":
                    message = String.format("Your package %s has been delivered successfully",
                            guide.getGuideNumber());
                    break;
                case "Incidencia":
                    message = String.format("There's an incident with your package %s. We'll contact you shortly",
                            guide.getGuideNumber());
                    break;
                default:
                    return; // No notification for other states
            }

            // Notify business
            if (newState.equals("Incidencia")) {
                notificationService.sendIncidentNotification(
                        guide.getBusiness().getEmail(),
                        "Delivery Update",
                        message,
                        guide.getGuideNumber());
            } else {
                notificationService.sendBusinessNotification(
                        guide.getBusiness().getEmail(),
                        "Delivery Update",
                        message);
            }

            // For "En Ruta" and "Entregada", also notify customer if phone is available
            if ((newState.equals("En Ruta") || newState.equals("Entregada")) &&
                    guide.getRecipientPhone() != null) {
                // SMS notification would be sent here if SMS service was implemented
                log.info("SMS notification would be sent to {} for guide {}",
                        guide.getRecipientPhone(), guide.getGuideId());
            }

        } catch (Exception e) {
            log.warn("Failed to send state change notifications for guide {}: {}",
                    guide.getGuideId(), e.getMessage());
        }
    }

    private CourierDeliveryDto mapToDeliveryDto(TrackingGuide guide) {
        // Check if the guide has any incidents
        boolean hasIncidents = deliveryIncidentRepository.existsByGuideId(guide.getGuideId());

        return CourierDeliveryDto.builder()
                .guideId(guide.getGuideId())
                .guideNumber(guide.getGuideNumber())
                .businessName(guide.getBusiness().getBusinessName())
                .currentState(guide.getCurrentState().getStateName())
                .basePrice(guide.getBasePrice())
                .courierCommission(guide.getCourierCommission())
                .recipientName(guide.getRecipientName())
                .recipientPhone(guide.getRecipientPhone())
                .recipientAddress(guide.getRecipientAddress())
                .recipientCity(guide.getRecipientCity())
                .recipientState(guide.getRecipientState())
                .observations(guide.getObservations())
                .assignmentAccepted(guide.getAssignmentAccepted())
                .assignmentDate(guide.getAssignmentDate())
                .assignmentAcceptedAt(guide.getAssignmentAcceptedAt())
                .pickupDate(guide.getPickupDate())
                .deliveryDate(guide.getDeliveryDate())
                .createdAt(guide.getCreatedAt())
                .priority("NORMAL")
                .hasIncidents(hasIncidents)
                .build();
    }
}
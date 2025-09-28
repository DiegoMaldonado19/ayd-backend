package com.ayd.sie.courier.application.usecases;

import com.ayd.sie.courier.application.dto.CourierDeliveryDto;
import com.ayd.sie.courier.application.dto.UpdateDeliveryStateDto;
import com.ayd.sie.shared.domain.entities.StateHistory;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.domain.entities.TrackingState;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.infrastructure.persistence.StateHistoryJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingStateJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
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
public class UpdateDeliveryStateOnlyUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final TrackingStateJpaRepository trackingStateRepository;
    private final StateHistoryJpaRepository stateHistoryRepository;
    private final UserJpaRepository userRepository;

    @Transactional
    public CourierDeliveryDto execute(Integer guideId, UpdateDeliveryStateDto request, Integer courierId) {
        log.info("Updating state only for guide {} to {} by courier {}", guideId, request.getNewState(), courierId);

        // 1. Validate courier
        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        if (!courier.getRole().getRoleName().equals("Repartidor")) {
            throw new BusinessConstraintViolationException("Only couriers can update delivery states");
        }

        // 2. Find guide
        TrackingGuide guide = trackingGuideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Guide not found"));

        // 3. Verify courier assignment
        if (guide.getCourier() == null || !guide.getCourier().getUserId().equals(courierId)) {
            throw new BusinessConstraintViolationException("Guide is not assigned to this courier");
        }

        // 4. Validate state transition
        String currentState = guide.getCurrentState().getStateName();
        String newState = request.getNewState();

        validateStateTransition(currentState, newState);

        // 5. Find new state
        TrackingState newTrackingState = trackingStateRepository.findByStateName(newState)
                .orElseThrow(() -> new ResourceNotFoundException("State not found: " + newState));

        // 6. Update guide state
        guide.setCurrentState(newTrackingState);

        // Update specific timestamps based on state
        LocalDateTime now = LocalDateTime.now();
        switch (newState) {
            case "Recogida" -> guide.setPickupDate(now);
            case "Entregada" -> guide.setDeliveryDate(now);
        }

        // Update observations if provided
        if (request.getObservations() != null && !request.getObservations().trim().isEmpty()) {
            guide.setObservations(request.getObservations());
        }

        trackingGuideRepository.save(guide);

        // 7. Create state history record
        StateHistory history = StateHistory.builder()
                .guide(guide)
                .state(newTrackingState)
                .user(courier)
                .changedAt(now)
                .observations(request.getObservations())
                .build();

        stateHistoryRepository.save(history);

        log.info("State updated successfully for guide {} from {} to {}", guideId, currentState, newState);

        // 8. Return updated delivery DTO
        return mapToCourierDeliveryDto(guide);
    }

    private void validateStateTransition(String currentState, String newState) {
        log.debug("Validating state transition from {} to {}", currentState, newState);

        // Basic state transition validation
        switch (currentState) {
            case "Asignada" -> {
                if (!"Recogida".equals(newState) && !"Incidencia".equals(newState)) {
                    throw new BusinessConstraintViolationException(
                            "From 'Asignada' state, can only transition to 'Recogida' or 'Incidencia'");
                }
            }
            case "Recogida" -> {
                if (!"En Ruta".equals(newState) && !"Incidencia".equals(newState)) {
                    throw new BusinessConstraintViolationException(
                            "From 'Recogida' state, can only transition to 'En Ruta' or 'Incidencia'");
                }
            }
            case "En Ruta" -> {
                if (!"Entregada".equals(newState) && !"Incidencia".equals(newState)) {
                    throw new BusinessConstraintViolationException(
                            "From 'En Ruta' state, can only transition to 'Entregada' or 'Incidencia'");
                }
            }
            case "Entregada", "Cancelada" -> {
                throw new BusinessConstraintViolationException(
                        "Cannot change state from final state: " + currentState);
            }
            case "Incidencia" -> {
                throw new BusinessConstraintViolationException(
                        "Cannot change state from 'Incidencia'. Please resolve the incident first.");
            }
            default -> throw new BusinessConstraintViolationException("Unknown current state: " + currentState);
        }
    }

    private CourierDeliveryDto mapToCourierDeliveryDto(TrackingGuide guide) {
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
                .priority("NORMAL") // Default value
                .hasIncidents(false) // This could be calculated by checking incidents
                .build();
    }
}
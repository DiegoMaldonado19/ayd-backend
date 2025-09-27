package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.AssignmentDto;
import com.ayd.sie.shared.domain.entities.*;
import com.ayd.sie.shared.domain.exceptions.BusinessConstraintViolationException;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.domain.services.NotificationService;
import com.ayd.sie.shared.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReassignDeliveryUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final UserJpaRepository userRepository;
    private final ContractJpaRepository contractRepository;
    private final StateHistoryJpaRepository stateHistoryRepository;
    private final NotificationService notificationService;

    @Transactional
    public AssignmentDto execute(Integer guideId, Integer newCourierId, String reason, Integer coordinatorId) {
        // 1. Validate coordinator
        User coordinator = userRepository.findById(coordinatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator not found"));

        if (!coordinator.getRole().getRoleName().equals("Coordinador")) {
            throw new BusinessConstraintViolationException("Only coordinators can reassign deliveries");
        }

        // 2. Validate and get tracking guide
        TrackingGuide guide = trackingGuideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Tracking guide not found"));

        // 3. Validate guide state - only allow reassignment before pickup
        String currentState = guide.getCurrentState().getStateName();
        if (!currentState.equals("Asignada") && !currentState.equals("Incidencia")) {
            throw new BusinessConstraintViolationException(
                    "Can only reassign deliveries in 'Asignada' or 'Incidencia' state. Current state: " + currentState);
        }

        // 4. Store previous courier for notifications
        User previousCourier = guide.getCourier();

        // 5. Validate new courier
        User newCourier = userRepository.findById(newCourierId)
                .orElseThrow(() -> new ResourceNotFoundException("New courier not found"));

        if (!newCourier.getRole().getRoleName().equals("Repartidor")) {
            throw new BusinessConstraintViolationException("Can only assign to couriers");
        }

        if (!Boolean.TRUE.equals(newCourier.getActive())) {
            throw new BusinessConstraintViolationException("Cannot assign to inactive courier");
        }

        // 6. CRITICAL: Validate new courier has active contract
        Contract activeContract = contractRepository.findActiveContractByUserId(newCourier.getUserId())
                .orElseThrow(() -> new BusinessConstraintViolationException(
                        "Cannot reassign delivery: New courier has no active contract"));

        // 7. Recalculate commission for new courier
        BigDecimal basePrice = BigDecimal.valueOf(guide.getBasePrice());
        BigDecimal commissionPercentage = activeContract.getCommissionPercentage();
        BigDecimal newCommission = basePrice.multiply(commissionPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 8. Update guide with new assignment
        guide.setCourier(newCourier);
        guide.setCoordinator(coordinator);
        guide.setCourierCommission(newCommission.doubleValue());
        guide.setAssignmentAccepted(false);
        guide.setAssignmentAcceptedAt(null);

        // 9. If guide was in incident state, move back to assigned
        if (currentState.equals("Incidencia")) {
            TrackingState assignedState = guide.getCurrentState(); // Should get assigned state from repository
            guide.setCurrentState(assignedState);
        }

        TrackingGuide savedGuide = trackingGuideRepository.save(guide);

        // 10. Create state history record
        String observations = String.format("Reassigned from %s to %s. Reason: %s",
                previousCourier != null ? previousCourier.getFirstName() + " " + previousCourier.getLastName()
                        : "unassigned",
                newCourier.getFirstName() + " " + newCourier.getLastName(),
                reason != null ? reason : "Coordinator decision");

        StateHistory stateHistory = StateHistory.builder()
                .guide(savedGuide)
                .state(savedGuide.getCurrentState())
                .user(coordinator)
                .observations(observations)
                .changedAt(LocalDateTime.now())
                .build();

        stateHistoryRepository.save(stateHistory);

        // 11. Send notifications
        try {
            // Notify new courier about assignment
            notificationService.sendAssignmentNotification(newCourier.getEmail(), savedGuide);

            // Notify previous courier about reassignment (if there was one)
            if (previousCourier != null) {
                notificationService.sendCourierNotification(
                        previousCourier.getEmail(),
                        "Entrega Reasignada",
                        String.format("La guía %s ha sido reasignada a otro repartidor",
                                savedGuide.getGuideNumber()));
            }

            // Notify business about reassignment
            notificationService.sendBusinessNotification(
                    savedGuide.getBusiness().getUser().getEmail(),
                    "Repartidor Cambiado",
                    String.format("Su guía %s ha sido reasignada al repartidor %s %s",
                            savedGuide.getGuideNumber(),
                            newCourier.getFirstName(),
                            newCourier.getLastName()));
        } catch (Exception e) {
            log.warn("Failed to send reassignment notifications: {}", e.getMessage());
        }

        log.info("Delivery reassigned - Guide: {}, Previous: {}, New: {}, Commission: {}",
                savedGuide.getGuideNumber(),
                previousCourier != null ? previousCourier.getEmail() : "none",
                newCourier.getEmail(),
                newCommission);

        // 12. Build and return response DTO
        return AssignmentDto.builder()
                .guideId(savedGuide.getGuideId())
                .guideNumber(savedGuide.getGuideNumber())
                .courierId(newCourier.getUserId())
                .courierName(newCourier.getFirstName() + " " + newCourier.getLastName())
                .coordinatorId(coordinator.getUserId())
                .coordinatorName(coordinator.getFirstName() + " " + coordinator.getLastName())
                .assignmentCriteria("REASSIGNMENT")
                .basePrice(savedGuide.getBasePrice())
                .courierCommission(savedGuide.getCourierCommission())
                .assignedAt(LocalDateTime.now())
                .assignmentAccepted(false)
                .businessName(savedGuide.getBusiness().getBusinessName())
                .recipientName(savedGuide.getRecipientName())
                .recipientAddress(savedGuide.getRecipientAddress())
                .currentState(savedGuide.getCurrentState().getStateName())
                .observations(reason)
                .build();
    }
}
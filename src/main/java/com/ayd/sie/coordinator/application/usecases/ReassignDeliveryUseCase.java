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
                                        "Can only reassign deliveries in 'Asignada' or 'Incidencia' state. Current state: "
                                                        + currentState);
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

                // 6. Validate new courier has active contract
                boolean hasActiveContract = contractRepository.hasActiveContractOnDate(
                                newCourier.getUserId(), java.time.LocalDate.now());

                if (!hasActiveContract) {
                        throw new BusinessConstraintViolationException("New courier does not have an active contract");
                }

                // 7. Calculate new courier commission (30% of base price)
                BigDecimal commissionRate = new BigDecimal("30");
                BigDecimal courierCommission = guide.getBasePrice()
                                .multiply(commissionRate)
                                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                // 8. Update guide
                guide.setCourier(newCourier);
                guide.setCoordinator(coordinator);
                guide.setCourierCommission(courierCommission);
                guide.setAssignmentDate(LocalDateTime.now());
                guide.setAssignmentAccepted(false);

                TrackingGuide savedGuide = trackingGuideRepository.save(guide);

                // 9. Record state history
                StateHistory stateHistory = StateHistory.builder()
                                .guide(savedGuide)
                                .state(savedGuide.getCurrentState())
                                .user(coordinator)
                                .observations(String.format("Reasignado de %s %s a %s %s. Razón: %s",
                                                previousCourier.getFirstName(), previousCourier.getLastName(),
                                                newCourier.getFirstName(), newCourier.getLastName(),
                                                reason != null ? reason : "No especificada"))
                                .changedAt(LocalDateTime.now())
                                .build();

                stateHistoryRepository.save(stateHistory);

                // 10. Send notifications
                try {
                        // Notify previous courier about reassignment
                        if (previousCourier != null) {
                                notificationService.sendCourierNotification(
                                                previousCourier.getEmail(),
                                                "Entrega Reasignada",
                                                String.format("La guía %s ha sido reasignada a otro repartidor",
                                                                savedGuide.getGuideNumber()));
                        }

                        // Notify new courier about assignment
                        notificationService.sendAssignmentNotification(
                                        newCourier.getEmail(),
                                        "Nueva Entrega Asignada",
                                        String.format("Se le ha asignado la guía %s para entrega a %s",
                                                        savedGuide.getGuideNumber(),
                                                        savedGuide.getRecipientName()),
                                        savedGuide.getGuideNumber());

                        // Notify business about reassignment
                        notificationService.sendBusinessNotification(
                                        savedGuide.getBusiness().getEmail(),
                                        "Entrega Reasignada",
                                        String.format("Su guía %s ha sido reasignada al repartidor %s %s",
                                                        savedGuide.getGuideNumber(),
                                                        newCourier.getFirstName(),
                                                        newCourier.getLastName()));
                } catch (Exception e) {
                        log.warn("Failed to send reassignment notifications: {}", e.getMessage());
                }

                log.info("Delivery reassigned - Guide: {}, New Courier: {}, Previous Courier: {}",
                                savedGuide.getGuideNumber(),
                                newCourier.getEmail(),
                                previousCourier != null ? previousCourier.getEmail() : "None");

                // 11. Build and return response DTO
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
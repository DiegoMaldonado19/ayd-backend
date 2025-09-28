package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.AssignDeliveryRequestDto;
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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignDeliveryUseCase {

        private final TrackingGuideJpaRepository trackingGuideRepository;
        private final UserJpaRepository userRepository;
        private final ContractJpaRepository contractRepository;
        private final TrackingStateJpaRepository trackingStateRepository;
        private final StateHistoryJpaRepository stateHistoryRepository;
        private final NotificationService notificationService;

        @Transactional
        public AssignmentDto execute(AssignDeliveryRequestDto request, Integer coordinatorId) {
                // 1. Validate coordinator
                User coordinator = userRepository.findById(coordinatorId)
                                .orElseThrow(() -> new ResourceNotFoundException("Coordinator not found"));

                if (!coordinator.getRole().getRoleName().equals("Coordinador")) {
                        throw new BusinessConstraintViolationException("Only coordinators can assign deliveries");
                }

                // 2. Validate and get tracking guide
                TrackingGuide guide = trackingGuideRepository.findById(request.getGuideId())
                                .orElseThrow(() -> new ResourceNotFoundException("Tracking guide not found"));

                // 3. Validate guide state - only allow assignment if guide is created (state 1)
                if (!guide.getCurrentState().getStateName().equals("Creada")) {
                        throw new BusinessConstraintViolationException(
                                        "Guide can only be assigned if it's in 'Creada' state. Current state: " +
                                                        guide.getCurrentState().getStateName());
                }

                // 4. Validate and get courier
                User courier = userRepository.findById(request.getCourierId())
                                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

                if (!courier.getRole().getRoleName().equals("Repartidor")) {
                        throw new BusinessConstraintViolationException("Can only assign to couriers");
                }

                if (!Boolean.TRUE.equals(courier.getActive())) {
                        throw new BusinessConstraintViolationException("Cannot assign to inactive courier");
                }

                // 5. Validate courier has active contract
                boolean hasActiveContract = contractRepository.hasActiveContractOnDate(
                                courier.getUserId(), LocalDate.now());

                if (!hasActiveContract) {
                        throw new BusinessConstraintViolationException("Courier does not have an active contract");
                }

                // 6. Calculate courier commission (30% of base price)
                BigDecimal commissionRate = new BigDecimal("30");
                BigDecimal courierCommission = guide.getBasePrice()
                                .multiply(commissionRate)
                                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                // 7. Get assigned state
                TrackingState assignedState = trackingStateRepository.findByStateName("Asignada")
                                .orElseThrow(() -> new ResourceNotFoundException("Assigned state not found"));

                // 8. Update guide
                guide.setCourier(courier);
                guide.setCoordinator(coordinator);
                guide.setCurrentState(assignedState);
                guide.setCourierCommission(courierCommission);
                guide.setAssignmentDate(LocalDateTime.now());
                guide.setAssignmentAccepted(false);

                TrackingGuide savedGuide = trackingGuideRepository.save(guide);

                // 9. Record state history
                StateHistory stateHistory = StateHistory.builder()
                                .guide(savedGuide)
                                .state(assignedState)
                                .user(coordinator)
                                .observations(request.getObservations() != null ? request.getObservations()
                                                : String.format("Asignado por %s usando criterio: %s",
                                                                coordinator.getFirstName() + " "
                                                                                + coordinator.getLastName(),
                                                                request.getAssignmentCriteria() != null
                                                                                ? request.getAssignmentCriteria()
                                                                                : "MANUAL"))
                                .changedAt(LocalDateTime.now())
                                .build();

                stateHistoryRepository.save(stateHistory);

                // 10. Send notifications
                try {
                        // Notify courier about new assignment
                        notificationService.sendAssignmentNotification(
                                        courier.getEmail(),
                                        "Nueva Entrega Asignada",
                                        String.format("Se le ha asignado la guía %s para entrega a %s",
                                                        savedGuide.getGuideNumber(),
                                                        savedGuide.getRecipientName()),
                                        savedGuide.getGuideNumber());

                        // Notify business about assignment
                        notificationService.sendBusinessNotification(
                                        savedGuide.getBusiness().getEmail(),
                                        "Entrega Asignada",
                                        String.format("Su guía %s ha sido asignada al repartidor %s %s",
                                                        savedGuide.getGuideNumber(),
                                                        courier.getFirstName(),
                                                        courier.getLastName()));
                } catch (Exception e) {
                        log.warn("Failed to send assignment notifications: {}", e.getMessage());
                }

                log.info("Delivery assigned - Guide: {}, Courier: {}, Commission: {}",
                                savedGuide.getGuideNumber(),
                                courier.getEmail(),
                                courierCommission);

                // 11. Build and return response DTO
                return AssignmentDto.builder()
                                .guideId(savedGuide.getGuideId())
                                .guideNumber(savedGuide.getGuideNumber())
                                .courierId(courier.getUserId())
                                .courierName(courier.getFirstName() + " " + courier.getLastName())
                                .coordinatorId(coordinator.getUserId())
                                .coordinatorName(coordinator.getFirstName() + " " + coordinator.getLastName())
                                .assignmentCriteria(request.getAssignmentCriteria())
                                .basePrice(savedGuide.getBasePrice())
                                .courierCommission(savedGuide.getCourierCommission())
                                .assignedAt(LocalDateTime.now())
                                .assignmentAccepted(false)
                                .businessName(savedGuide.getBusiness().getBusinessName())
                                .recipientName(savedGuide.getRecipientName())
                                .recipientAddress(savedGuide.getRecipientAddress())
                                .currentState(savedGuide.getCurrentState().getStateName())
                                .observations(request.getObservations())
                                .build();
        }
}
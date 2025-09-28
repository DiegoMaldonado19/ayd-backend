package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.RescheduleDto;
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
public class RescheduleDeliveryUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final UserJpaRepository userRepository;
    private final ContractJpaRepository contractRepository;
    private final StateHistoryJpaRepository stateHistoryRepository;
    private final TrackingStateJpaRepository trackingStateRepository;
    private final NotificationService notificationService;

    @Transactional
    public RescheduleDto execute(Integer guideId, RescheduleDto request, Integer coordinatorId) {
        // 1. Validate coordinator
        User coordinator = userRepository.findById(coordinatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator not found"));

        if (!coordinator.getRole().getRoleName().equals("Coordinador")) {
            throw new BusinessConstraintViolationException("Only coordinators can reschedule deliveries");
        }

        // 2. Validate and get tracking guide
        TrackingGuide guide = trackingGuideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Tracking guide not found"));

        // 3. Validate guide state - only allow rescheduling for certain states
        String currentState = guide.getCurrentState().getStateName();
        if (!currentState.equals("Asignada") && !currentState.equals("Incidencia") &&
                !currentState.equals("Creada")) {
            throw new BusinessConstraintViolationException(
                    "Can only reschedule deliveries in 'Creada', 'Asignada', or 'Incidencia' state. Current state: "
                            + currentState);
        }

        // 4. Store previous values for tracking
        User previousCourier = guide.getCourier();
        LocalDateTime previousDeliveryDate = guide.getDeliveryDate();

        // 5. Handle courier change if requested
        User newCourier = null;
        if (request.getNewCourierId() != null) {
            newCourier = userRepository.findById(request.getNewCourierId())
                    .orElseThrow(() -> new ResourceNotFoundException("New courier not found"));

            if (!newCourier.getRole().getRoleName().equals("Repartidor")) {
                throw new BusinessConstraintViolationException("Can only assign to couriers");
            }

            if (!Boolean.TRUE.equals(newCourier.getActive())) {
                throw new BusinessConstraintViolationException("Cannot assign to inactive courier");
            }

            // CRITICAL: Validate new courier has active contract
            contractRepository.findActiveContractByUserId(newCourier.getUserId())
                    .orElseThrow(() -> new BusinessConstraintViolationException(
                            "Cannot reschedule to courier: New courier has no active contract"));

            guide.setCourier(newCourier);
            guide.setAssignmentAccepted(false);
            guide.setAssignmentAcceptedAt(null);
        }

        // 6. Update delivery date if provided
        if (request.getNewDeliveryDate() != null) {
            guide.setDeliveryDate(request.getNewDeliveryDate());
        }

        // 7. Ensure guide is in appropriate state after reschedule
        if (currentState.equals("Incidencia")) {
            TrackingState assignedState = trackingStateRepository.findByStateName("Asignada")
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned state not found"));
            guide.setCurrentState(assignedState);
        }

        TrackingGuide savedGuide = trackingGuideRepository.save(guide);

        // 8. Create state history record
        StringBuilder observations = new StringBuilder("Delivery rescheduled. ");
        if (request.getReason() != null) {
            observations.append("Reason: ").append(request.getReason()).append(". ");
        }
        if (newCourier != null) {
            observations.append("Courier changed to: ")
                    .append(newCourier.getFirstName()).append(" ").append(newCourier.getLastName()).append(". ");
        }
        if (request.getNewDeliveryDate() != null) {
            observations.append("New delivery date: ").append(request.getNewDeliveryDate()).append(". ");
        }

        StateHistory stateHistory = StateHistory.builder()
                .guide(savedGuide)
                .state(savedGuide.getCurrentState())
                .user(coordinator)
                .observations(observations.toString())
                .changedAt(LocalDateTime.now())
                .build();

        stateHistoryRepository.save(stateHistory);

        // 9. Send notifications
        boolean customerNotified = false;
        try {
            // Notify current/new courier
            User currentCourier = savedGuide.getCourier();
            if (currentCourier != null) {
                String message = String.format("La entrega %s ha sido reprogramada", savedGuide.getGuideNumber());
                if (request.getNewDeliveryDate() != null) {
                    message += " para " + request.getNewDeliveryDate();
                }
                notificationService.sendCourierNotification(currentCourier.getEmail(), "Entrega Reprogramada", message);
            }

            // Notify previous courier if changed
            if (newCourier != null && previousCourier != null && !previousCourier.equals(newCourier)) {
                notificationService.sendCourierNotification(
                        previousCourier.getEmail(),
                        "Entrega Reasignada",
                        String.format("La guía %s ha sido reasignada debido a reprogramación",
                                savedGuide.getGuideNumber()));
            }

            // Notify business
            String businessMessage = String.format("Su entrega %s ha sido reprogramada", savedGuide.getGuideNumber());
            if (newCourier != null) {
                businessMessage += " y asignada a " + newCourier.getFirstName() + " " + newCourier.getLastName();
            }
            notificationService.sendBusinessNotification(
                    savedGuide.getBusiness().getUser().getEmail(),
                    "Entrega Reprogramada",
                    businessMessage);

            // Notify customer if contact information is available
            if (savedGuide.getRecipientPhone() != null && !savedGuide.getRecipientPhone().trim().isEmpty()) {
                String customerMessage = String.format("Su entrega %s ha sido reprogramada.",
                        savedGuide.getGuideNumber());
                if (newCourier != null) {
                    customerMessage += String.format(" Nuevo repartidor asignado: %s %s",
                            newCourier.getFirstName(), newCourier.getLastName());
                }
                customerMessage += " Nos pondremos en contacto con usted para coordinar la nueva fecha de entrega.";

                // We'll use the business notification method as a simple email sender
                // In a real scenario, we could create a specific customer notification method
                // or use SMS service for phone notifications
                notificationService.sendBusinessNotification(
                        savedGuide.getBusiness().getBusinessEmail() != null
                                ? savedGuide.getBusiness().getBusinessEmail()
                                : savedGuide.getBusiness().getUser().getEmail(),
                        "Notificación para Cliente - Entrega Reprogramada",
                        String.format("PARA CLIENTE: %s (Tel: %s)%n%nMensaje: %s",
                                savedGuide.getRecipientName(),
                                savedGuide.getRecipientPhone(),
                                customerMessage));

                log.info("Customer notification attempted for guide: {}, recipient: {}",
                        savedGuide.getGuideNumber(), savedGuide.getRecipientName());
            } else {
                log.warn("Customer contact information not available for guide: {}",
                        savedGuide.getGuideNumber());
            }
            customerNotified = true;
        } catch (Exception e) {
            log.warn("Failed to send reschedule notifications: {}", e.getMessage());
        }

        log.info("Delivery rescheduled - Guide: {}, Previous courier: {}, New courier: {}",
                savedGuide.getGuideNumber(),
                previousCourier != null ? previousCourier.getEmail() : "none",
                newCourier != null ? newCourier.getEmail() : "same");

        // 10. Build and return response DTO
        return RescheduleDto.builder()
                .guideId(savedGuide.getGuideId())
                .guideNumber(savedGuide.getGuideNumber())
                .newCourierId(newCourier != null ? newCourier.getUserId() : null)
                .newCourierName(newCourier != null ? newCourier.getFirstName() + " " + newCourier.getLastName() : null)
                .previousCourierId(previousCourier != null ? previousCourier.getUserId() : null)
                .previousCourierName(
                        previousCourier != null ? previousCourier.getFirstName() + " " + previousCourier.getLastName()
                                : null)
                .newDeliveryDate(request.getNewDeliveryDate())
                .previousDeliveryDate(previousDeliveryDate)
                .reason(request.getReason())
                .coordinatorId(coordinator.getUserId())
                .coordinatorName(coordinator.getFirstName() + " " + coordinator.getLastName())
                .businessName(savedGuide.getBusiness().getBusinessName())
                .recipientName(savedGuide.getRecipientName())
                .recipientPhone(savedGuide.getRecipientPhone())
                .recipientAddress(savedGuide.getRecipientAddress())
                .currentState(savedGuide.getCurrentState().getStateName())
                .rescheduledAt(LocalDateTime.now())
                .customerNotified(customerNotified)
                .notes(request.getNotes())
                .build();
    }
}
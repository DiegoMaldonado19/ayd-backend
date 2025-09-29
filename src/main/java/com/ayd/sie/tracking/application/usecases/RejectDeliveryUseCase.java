package com.ayd.sie.tracking.application.usecases;

import com.ayd.sie.shared.domain.entities.*;
import com.ayd.sie.shared.infrastructure.persistence.*;
import com.ayd.sie.shared.infrastructure.notifications.EmailService;
import com.ayd.sie.tracking.application.dto.RejectDeliveryDto;
import com.ayd.sie.tracking.application.dto.RejectDeliveryResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RejectDeliveryUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final TrackingStateJpaRepository trackingStateRepository;
    private final StateHistoryJpaRepository stateHistoryRepository;
    private final CancellationJpaRepository cancellationRepository;
    private final CancellationTypeJpaRepository cancellationTypeRepository;
    private final EmailService emailService;

    public RejectDeliveryResponseDto rejectDelivery(RejectDeliveryDto request) {
        log.info("Processing delivery rejection for guide: {}", request.getGuideNumber());

        // Find the tracking guide
        TrackingGuide guide = trackingGuideRepository.findByGuideNumber(request.getGuideNumber())
                .orElseThrow(() -> new RuntimeException("Tracking guide not found: " + request.getGuideNumber()));

        // Validate that delivery can be rejected
        validateCanRejectDelivery(guide);

        // Get rejected state
        TrackingState rejectedState = trackingStateRepository.findByStateName("Rechazada")
                .orElseThrow(() -> new RuntimeException("Rejected state not found"));

        // Get customer cancellation type
        CancellationType customerCancellation = cancellationTypeRepository.findByTypeNameAndActiveTrue("Cliente")
                .orElseThrow(() -> new RuntimeException("Customer cancellation type not found"));

        // Update guide status to rejected
        guide.setCurrentState(rejectedState);
        guide.setCancellationDate(LocalDateTime.now());
        guide.setObservations(guide.getObservations() != null
                ? guide.getObservations() + " | Rechazado: " + request.getRejectionReason()
                : "Rechazado: " + request.getRejectionReason());

        trackingGuideRepository.save(guide);

        // Record state change in history
        recordStateHistory(guide, rejectedState, request.getRejectionReason());

        // Create cancellation record
        createCancellationRecord(guide, customerCancellation, request);

        // Send notifications
        sendRejectionNotifications(guide, request.getRejectionReason());

        // Determine if return process should be initiated
        boolean returnProcessInitiated = initializeReturnProcessIfNeeded(guide, request.getRequiresReturn());

        return RejectDeliveryResponseDto.builder()
                .guideNumber(guide.getGuideNumber())
                .status(rejectedState.getStateName())
                .message("Delivery has been rejected successfully")
                .returnProcessInitiated(returnProcessInitiated)
                .build();
    }

    private void validateCanRejectDelivery(TrackingGuide guide) {
        String currentStatus = guide.getCurrentState().getStateName();
        boolean isFinal = guide.getCurrentState().getIsFinal();

        if (isFinal) {
            throw new RuntimeException("Cannot reject delivery in final state: " + currentStatus);
        }

        // Can only reject if in certain states
        if (!currentStatus.equals("En Ruta") &&
                !currentStatus.equals("Entrega Proxima") &&
                !currentStatus.equals("Asignada") &&
                !currentStatus.equals("Recogida")) {
            throw new RuntimeException("Cannot reject delivery in current state: " + currentStatus);
        }
    }

    private void recordStateHistory(TrackingGuide guide, TrackingState newState, String reason) {
        StateHistory history = StateHistory.builder()
                .guide(guide)
                .state(newState)
                .user(null) // Public rejection, no user
                .observations("Delivery rejected by recipient: " + reason)
                .changedAt(LocalDateTime.now())
                .build();

        stateHistoryRepository.save(history);
    }

    private void createCancellationRecord(TrackingGuide guide, CancellationType cancellationType,
            RejectDeliveryDto request) {
        Cancellation cancellation = Cancellation.builder()
                .guide(guide)
                .cancellationType(cancellationType)
                .cancelledByUser(null) // Public rejection, no specific user
                .reason("Customer rejection: " + request.getRejectionReason())
                .penaltyAmount(BigDecimal.ZERO) // No penalty for customer rejection
                .courierCommission(BigDecimal.ZERO)
                .cancelledAt(LocalDateTime.now())
                .build();

        cancellationRepository.save(cancellation);
    }

    private void sendRejectionNotifications(TrackingGuide guide, String reason) {
        try {
            // Notify business
            String businessSubject = "Delivery Rejected - Guide " + guide.getGuideNumber();
            String businessMessage = String.format(
                    "The delivery with guide number %s has been rejected by the recipient.\\n" +
                            "Recipient: %s\\n" +
                            "Reason: %s\\n" +
                            "Address: %s, %s, %s",
                    guide.getGuideNumber(),
                    guide.getRecipientName(),
                    reason,
                    guide.getRecipientAddress(),
                    guide.getRecipientCity(),
                    guide.getRecipientState());

            // Send to business owner
            if (guide.getBusiness() != null) {
                String businessEmail = guide.getBusiness().getEmail(); // Use the helper method from Business entity
                if (businessEmail != null) {
                    emailService.sendSimpleEmail(businessEmail, businessSubject, businessMessage);
                }
            }

            // Notify courier if assigned
            if (guide.getCourier() != null) {
                String courierSubject = "Delivery Rejected - Guide " + guide.getGuideNumber();
                String courierMessage = String.format(
                        "The delivery you were assigned (Guide: %s) has been rejected by the recipient.\\n" +
                                "Reason: %s\\n" +
                                "Please contact your coordinator for further instructions.",
                        guide.getGuideNumber(),
                        reason);
                emailService.sendSimpleEmail(guide.getCourier().getEmail(), courierSubject, courierMessage);
            }

            // Notify coordinator if assigned
            if (guide.getCoordinator() != null) {
                String coordinatorSubject = "Delivery Rejected - Guide " + guide.getGuideNumber();
                String coordinatorMessage = String.format(
                        "Delivery with guide number %s has been rejected.\\n" +
                                "Courier: %s\\n" +
                                "Reason: %s",
                        guide.getGuideNumber(),
                        guide.getCourier() != null
                                ? guide.getCourier().getFirstName() + " " + guide.getCourier().getLastName()
                                : "Not assigned",
                        reason);
                emailService.sendSimpleEmail(guide.getCoordinator().getEmail(), coordinatorSubject, coordinatorMessage);
            }

        } catch (Exception e) {
            log.error("Error sending rejection notifications for guide {}: {}", guide.getGuideNumber(), e.getMessage());
            // Don't fail the whole operation if notification fails
        }
    }

    private boolean initializeReturnProcessIfNeeded(TrackingGuide guide, Boolean requiresReturn) {
        if (!Boolean.TRUE.equals(requiresReturn)) {
            return false;
        }

        try {
            // Here you would typically create a return guide or update the existing guide
            // For now, we'll just update the observations
            String currentObservations = guide.getObservations() != null ? guide.getObservations() : "";
            guide.setObservations(currentObservations + " | Return process initiated");
            trackingGuideRepository.save(guide);

            log.info("Return process initiated for guide: {}", guide.getGuideNumber());
            return true;
        } catch (Exception e) {
            log.error("Error initializing return process for guide {}: {}", guide.getGuideNumber(), e.getMessage());
            return false;
        }
    }
}
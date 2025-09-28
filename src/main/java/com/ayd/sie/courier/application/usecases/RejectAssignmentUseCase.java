package com.ayd.sie.courier.application.usecases;

import com.ayd.sie.courier.application.dto.RejectAssignmentDto;
import com.ayd.sie.shared.domain.entities.StateHistory;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.domain.entities.TrackingState;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.infrastructure.persistence.StateHistoryJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingStateJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
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
public class RejectAssignmentUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final UserJpaRepository userRepository;
    private final TrackingStateJpaRepository trackingStateRepository;
    private final StateHistoryJpaRepository stateHistoryRepository;
    private final NotificationService notificationService;

    @Transactional
    public RejectAssignmentDto execute(RejectAssignmentDto request, Integer courierId) {
        log.info("Courier {} rejecting assignment for guide {}", courierId, request.getGuideId());

        // 1. Validate courier
        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        if (!courier.getRole().getRoleName().equals("Repartidor")) {
            throw new BusinessConstraintViolationException("Only couriers can reject assignments");
        }

        if (!Boolean.TRUE.equals(courier.getActive())) {
            throw new BusinessConstraintViolationException("Inactive courier cannot reject assignments");
        }

        // 2. Validate and get tracking guide
        TrackingGuide guide = trackingGuideRepository.findById(request.getGuideId())
                .orElseThrow(() -> new ResourceNotFoundException("Tracking guide not found"));

        // 3. Validate guide state - only allow rejection if assigned to this courier
        // and not accepted
        if (!guide.getCurrentState().getStateName().equals("Asignada")) {
            throw new BusinessConstraintViolationException(
                    "Can only reject guides in 'Asignada' state. Current state: "
                            + guide.getCurrentState().getStateName());
        }

        // 4. Verify the guide is assigned to the requesting courier
        if (guide.getCourier() == null || !guide.getCourier().getUserId().equals(courierId)) {
            throw new BusinessConstraintViolationException("Guide is not assigned to this courier");
        }

        // 5. Check if already accepted (cannot reject if already accepted)
        if (Boolean.TRUE.equals(guide.getAssignmentAccepted())) {
            throw new BusinessConstraintViolationException("Cannot reject assignment that has already been accepted");
        }

        // 6. Get "Creada" state to reset the guide
        TrackingState createdState = trackingStateRepository.findByStateName("Creada")
                .orElseThrow(() -> new ResourceNotFoundException("Created state not found"));

        // 7. Store courier reference before removing from guide (to use in state
        // history)
        User rejectedByCourier = courier;

        // 8. Update guide - remove courier assignment and reset to created state
        guide.setCourier(null);
        // NOTE: We keep the coordinator assigned to maintain referential integrity
        // for the database trigger that requires either courier_id or coordinator_id
        guide.setCourierCommission(null);
        guide.setCurrentState(createdState);
        guide.setAssignmentAccepted(null);
        guide.setAssignmentAcceptedAt(null);

        TrackingGuide savedGuide = trackingGuideRepository.save(guide);

        // 9. Record state history for rejection
        StateHistory stateHistory = StateHistory.builder()
                .guide(savedGuide)
                .state(createdState)
                .user(rejectedByCourier) // Use stored reference to avoid null
                .observations(String.format("Assignment rejected by courier: %s. Additional notes: %s",
                        request.getRejectionReason(),
                        request.getAdditionalNotes() != null ? request.getAdditionalNotes() : "None"))
                .changedAt(LocalDateTime.now())
                .build();

        stateHistoryRepository.save(stateHistory);

        // 10. Send notifications
        try {
            // Notify coordinator about rejection (if there was one assigned)
            if (guide.getCoordinator() != null) {
                notificationService.sendAssignmentNotification(
                        guide.getCoordinator().getEmail(),
                        "Assignment Rejected",
                        String.format("Courier %s %s has rejected assignment for guide %s. Reason: %s",
                                courier.getFirstName(), courier.getLastName(),
                                guide.getGuideNumber(), request.getRejectionReason()),
                        guide.getGuideNumber());
            }

            // Notify business about rejection
            notificationService.sendBusinessNotification(
                    guide.getBusiness().getEmail(),
                    "Delivery Assignment Rejected",
                    String.format(
                            "The assignment for your package %s was rejected and will be reassigned to another courier",
                            guide.getGuideNumber()));

            // If courier is unavailable for reassignment, notify coordinators
            if (Boolean.TRUE.equals(request.getUnavailableForReassignment())) {
                log.info("Courier {} marked as unavailable for reassignment", courierId);
                // Additional logic could be added here to mark courier as temporarily
                // unavailable
            }
        } catch (Exception e) {
            log.warn("Failed to send rejection notifications for guide {}: {}", guide.getGuideId(), e.getMessage());
        }

        log.info("Assignment rejected successfully for guide {} by courier {}", guide.getGuideId(), courierId);

        // Return the updated request with success confirmation
        return RejectAssignmentDto.builder()
                .guideId(savedGuide.getGuideId())
                .rejectionReason(request.getRejectionReason())
                .additionalNotes(request.getAdditionalNotes())
                .currentLocation(request.getCurrentLocation())
                .unavailableForReassignment(request.getUnavailableForReassignment())
                .rejectedAt(LocalDateTime.now())
                .build();
    }
}
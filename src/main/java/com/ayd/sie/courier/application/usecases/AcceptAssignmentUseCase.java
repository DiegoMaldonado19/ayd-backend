package com.ayd.sie.courier.application.usecases;

import com.ayd.sie.courier.application.dto.AcceptAssignmentDto;
import com.ayd.sie.shared.domain.entities.StateHistory;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.infrastructure.persistence.StateHistoryJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
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
public class AcceptAssignmentUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final UserJpaRepository userRepository;
    private final StateHistoryJpaRepository stateHistoryRepository;
    private final NotificationService notificationService;

    @Transactional
    public AcceptAssignmentDto execute(AcceptAssignmentDto request, Integer courierId) {
        log.info("Courier {} accepting assignment for guide {}", courierId, request.getGuideId());

        // 1. Validate courier
        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        if (!courier.getRole().getRoleName().equals("Repartidor")) {
            throw new BusinessConstraintViolationException("Only couriers can accept assignments");
        }

        if (!Boolean.TRUE.equals(courier.getActive())) {
            throw new BusinessConstraintViolationException("Inactive courier cannot accept assignments");
        }

        // 2. Validate and get tracking guide
        TrackingGuide guide = trackingGuideRepository.findById(request.getGuideId())
                .orElseThrow(() -> new ResourceNotFoundException("Tracking guide not found"));

        // 3. Validate guide state - only allow acceptance if assigned to this courier
        if (!guide.getCurrentState().getStateName().equals("Asignada")) {
            throw new BusinessConstraintViolationException(
                    "Can only accept guides in 'Asignada' state. Current state: "
                            + guide.getCurrentState().getStateName());
        }

        // 4. Verify the guide is assigned to the requesting courier
        if (guide.getCourier() == null || !guide.getCourier().getUserId().equals(courierId)) {
            throw new BusinessConstraintViolationException("Guide is not assigned to this courier");
        }

        // 5. Check if already accepted
        if (Boolean.TRUE.equals(guide.getAssignmentAccepted())) {
            throw new BusinessConstraintViolationException("Assignment has already been accepted");
        }

        // 6. Update guide acceptance
        guide.setAssignmentAccepted(true);
        guide.setAssignmentAcceptedAt(LocalDateTime.now());

        TrackingGuide savedGuide = trackingGuideRepository.save(guide);

        // 7. Record state history for acceptance
        StateHistory stateHistory = StateHistory.builder()
                .guide(savedGuide)
                .state(guide.getCurrentState())
                .user(courier)
                .observations(request.getAcceptanceNotes() != null
                        ? String.format("Assignment accepted: %s", request.getAcceptanceNotes())
                        : "Assignment accepted by courier")
                .changedAt(LocalDateTime.now())
                .build();

        stateHistoryRepository.save(stateHistory);

        // 8. Send notifications
        try {
            // Notify coordinator about acceptance
            if (guide.getCoordinator() != null) {
                notificationService.sendAssignmentNotification(
                        guide.getCoordinator().getEmail(),
                        "Assignment Accepted",
                        String.format("Courier %s %s has accepted assignment for guide %s",
                                courier.getFirstName(), courier.getLastName(), guide.getGuideNumber()),
                        guide.getGuideNumber());
            }

            // Notify business about acceptance
            notificationService.sendBusinessNotification(
                    guide.getBusiness().getEmail(),
                    "Delivery Assignment Accepted",
                    String.format("Your package %s has been accepted by our courier and will be picked up shortly",
                            guide.getGuideNumber()));
        } catch (Exception e) {
            log.warn("Failed to send acceptance notifications for guide {}: {}", guide.getGuideId(), e.getMessage());
        }

        log.info("Assignment accepted successfully for guide {} by courier {}", guide.getGuideId(), courierId);

        // Return the updated request with success confirmation
        return AcceptAssignmentDto.builder()
                .guideId(savedGuide.getGuideId())
                .acceptanceNotes(request.getAcceptanceNotes())
                .estimatedPickupTime(request.getEstimatedPickupTime())
                .acceptedAt(savedGuide.getAssignmentAcceptedAt())
                .build();
    }
}
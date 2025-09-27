package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.CancellationDto;
import com.ayd.sie.coordinator.application.dto.ProcessCancellationRequestDto;
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
public class ProcessCancellationUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final UserJpaRepository userRepository;
    private final TrackingStateJpaRepository trackingStateRepository;
    private final StateHistoryJpaRepository stateHistoryRepository;
    private final CancellationJpaRepository cancellationRepository;
    private final CancellationTypeJpaRepository cancellationTypeRepository;
    private final NotificationService notificationService;

    @Transactional
    public CancellationDto execute(ProcessCancellationRequestDto request, Integer coordinatorId) {
        // 1. Validate coordinator
        User coordinator = userRepository.findById(coordinatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator not found"));

        if (!coordinator.getRole().getRoleName().equals("Coordinador")) {
            throw new BusinessConstraintViolationException("Only coordinators can process cancellations");
        }

        // 2. Validate and get tracking guide
        TrackingGuide guide = trackingGuideRepository.findById(request.getGuideId())
                .orElseThrow(() -> new ResourceNotFoundException("Tracking guide not found"));

        // 3. CRITICAL: Validate cancellation is allowed (not after pickup)
        String currentState = guide.getCurrentState().getStateName();
        if (currentState.equals("Recogida") || currentState.equals("En Ruta") ||
                currentState.equals("Entregada") || currentState.equals("Rechazada")) {
            throw new BusinessConstraintViolationException(
                    "Cannot cancel delivery after pickup. Current state: " + currentState);
        }

        if (currentState.equals("Cancelada")) {
            throw new BusinessConstraintViolationException("Delivery is already cancelled");
        }

        // 4. Validate cancellation type
        CancellationType cancellationType = cancellationTypeRepository.findById(request.getCancellationTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Cancellation type not found"));

        // 5. Get cancelled state
        TrackingState cancelledState = trackingStateRepository.findByStateName("Cancelada")
                .orElseThrow(() -> new ResourceNotFoundException("Cancelled state not found"));

        // 6. Calculate penalty based on business loyalty level
        Business business = guide.getBusiness();
        LoyaltyLevel loyaltyLevel = business.getCurrentLevel();

        Double penaltyPercentage = 0.0;
        Double commissionPenalty = 0.0;

        if (request.getApplyPenalty() && guide.getCourier() != null && guide.getCourierCommission() != null) {
            penaltyPercentage = loyaltyLevel.getPenaltyPercentage();

            // Calculate commission penalty
            BigDecimal commission = BigDecimal.valueOf(guide.getCourierCommission());
            BigDecimal penalty = commission.multiply(BigDecimal.valueOf(penaltyPercentage))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            commissionPenalty = penalty.doubleValue();
        }

        // 7. Get requester user if provided
        User requestedBy = null;
        if (request.getRequestedByUserId() != null) {
            requestedBy = userRepository.findById(request.getRequestedByUserId())
                    .orElse(null);
        }

        // 8. Create cancellation record
        Cancellation cancellation = Cancellation.builder()
                .guide(guide)
                .cancellationType(cancellationType)
                .reason(request.getReason())
                .requestedByUser(requestedBy)
                .processedByUser(coordinator)
                .penaltyPercentage(penaltyPercentage)
                .commissionPenalty(commissionPenalty)
                .processedAt(LocalDateTime.now())
                .build();

        Cancellation savedCancellation = cancellationRepository.save(cancellation);

        // 9. Update guide state
        String previousState = guide.getCurrentState().getStateName();
        guide.setCurrentState(cancelledState);
        TrackingGuide savedGuide = trackingGuideRepository.save(guide);

        // 10. Create state history record
        StateHistory stateHistory = StateHistory.builder()
                .guide(savedGuide)
                .state(cancelledState)
                .user(coordinator)
                .observations(String.format("Cancelled by %s: %s | Penalty: %.2f%%",
                        cancellationType.getTypeName(), request.getReason(), penaltyPercentage))
                .changedAt(LocalDateTime.now())
                .build();

        stateHistoryRepository.save(stateHistory);

        // 11. Send notifications
        try {
            if (request.getNotifyBusiness() && business.getUser() != null) {
                notificationService.sendBusinessNotification(
                        business.getUser().getEmail(),
                        "Entrega Cancelada",
                        String.format("Su guía %s ha sido cancelada. Penalty aplicado: %.2f%%",
                                savedGuide.getGuideNumber(), penaltyPercentage));
            }

            if (request.getNotifyCourier() && savedGuide.getCourier() != null) {
                notificationService.sendCourierNotification(
                        savedGuide.getCourier().getEmail(),
                        "Entrega Cancelada",
                        String.format("La guía %s asignada a ti ha sido cancelada",
                                savedGuide.getGuideNumber()));
            }

            if (request.getNotifyCustomer()) {
                // For customer notification, we would need customer contact info
                // This would be implemented based on how customer notifications are handled
            }
        } catch (Exception e) {
            log.warn("Failed to send cancellation notifications: {}", e.getMessage());
        }

        log.info("Delivery cancelled - Guide: {}, Type: {}, Penalty: {}%, Commission penalty: {}",
                savedGuide.getGuideNumber(),
                cancellationType.getTypeName(),
                penaltyPercentage,
                commissionPenalty);

        // 12. Build and return response DTO
        return CancellationDto.builder()
                .cancellationId(savedCancellation.getCancellationId())
                .guideId(savedGuide.getGuideId())
                .guideNumber(savedGuide.getGuideNumber())
                .cancellationTypeId(cancellationType.getCancellationTypeId())
                .cancellationTypeName(cancellationType.getTypeName())
                .reason(savedCancellation.getReason())
                .requestedByUserId(requestedBy != null ? requestedBy.getUserId() : null)
                .requestedByName(
                        requestedBy != null ? requestedBy.getFirstName() + " " + requestedBy.getLastName() : null)
                .requestedByRole(requestedBy != null ? requestedBy.getRole().getRoleName() : null)
                .processedByCoordinatorId(coordinator.getUserId())
                .processedByCoordinatorName(coordinator.getFirstName() + " " + coordinator.getLastName())
                .courierId(savedGuide.getCourier() != null ? savedGuide.getCourier().getUserId() : null)
                .courierName(savedGuide.getCourier() != null
                        ? savedGuide.getCourier().getFirstName() + " " + savedGuide.getCourier().getLastName()
                        : null)
                .businessId(business.getBusinessId())
                .businessName(business.getBusinessName())
                .businessLoyaltyLevel(loyaltyLevel.getLevelName())
                .penaltyPercentage(penaltyPercentage)
                .commissionPenalty(commissionPenalty)
                .basePrice(savedGuide.getBasePrice())
                .originalCommission(savedGuide.getCourierCommission())
                .previousState(previousState)
                .recipientName(savedGuide.getRecipientName())
                .recipientAddress(savedGuide.getRecipientAddress())
                .cancelledAt(savedCancellation.getProcessedAt())
                .customerNotified(request.getNotifyCustomer())
                .courierNotified(request.getNotifyCourier())
                .build();
    }
}
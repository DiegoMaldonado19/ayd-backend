package com.ayd.sie.business.application.usecases;

import com.ayd.sie.business.application.dto.CancelGuideDto;
import com.ayd.sie.business.application.dto.CancellationResponseDto;
import com.ayd.sie.shared.domain.entities.*;
import com.ayd.sie.shared.domain.exceptions.BusinessConstraintViolationException;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.*;
import com.ayd.sie.shared.domain.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CancelDeliveryUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final BusinessJpaRepository businessRepository;
    private final TrackingStateJpaRepository trackingStateRepository;
    private final CancellationTypeJpaRepository cancellationTypeRepository;
    private final CancellationJpaRepository cancellationRepository;
    private final UserJpaRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public CancellationResponseDto execute(Integer guideId, CancelGuideDto dto, Integer businessId) {
        // Validate business exists and is active
        Business business = businessRepository.findByUserUserIdAndActiveTrue(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        // Find guide
        TrackingGuide guide = trackingGuideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Guide not found"));

        // Validate guide belongs to business
        if (!guide.getBusiness().getBusinessId().equals(business.getBusinessId())) {
            throw new BusinessConstraintViolationException("Guide does not belong to the business");
        }

        // Validate guide can be cancelled (only before pickup)
        if (guide.getPickupDate() != null) {
            throw new BusinessConstraintViolationException("Cannot cancel guide after pickup");
        }

        // Check if guide is already cancelled
        if ("Cancelada".equals(guide.getCurrentState().getStateName())) {
            throw new BusinessConstraintViolationException("Guide is already cancelled");
        }

        // Get cancelled state
        TrackingState cancelledState = trackingStateRepository.findByStateName("Cancelada")
                .orElseThrow(() -> new ResourceNotFoundException("Cancelled state not found"));

        // Get cancellation type (business cancellation)
        CancellationType cancellationType = cancellationTypeRepository
                .findByTypeNameAndActiveTrue("Cancelación por Comercio")
                .orElse(cancellationTypeRepository.findByActiveTrueOrderByTypeName().get(0)); // Use first available
                                                                                              // type as fallback

        // Calculate penalty based on loyalty level
        BigDecimal penaltyAmount = calculatePenaltyAmount(guide, business);

        // Update guide state
        guide.setCurrentState(cancelledState);
        guide.setCancellationDate(LocalDateTime.now());
        TrackingGuide updatedGuide = trackingGuideRepository.save(guide);

        // Create cancellation record
        User businessUser = userRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business user not found"));

        Cancellation cancellation = Cancellation.builder()
                .guide(updatedGuide)
                .cancelledByUser(businessUser)
                .cancellationType(cancellationType)
                .reason(dto.getCancellation_reason())
                .penaltyAmount(penaltyAmount)
                .courierCommission(
                        guide.getCourierCommission() != null ? guide.getCourierCommission() : BigDecimal.ZERO)
                .cancelledAt(LocalDateTime.now())
                .build();

        Cancellation savedCancellation = cancellationRepository.save(cancellation);

        // Send notifications
        try {
            // Notify business
            notificationService.sendCancellationNotification(
                    business.getEmail(),
                    "Guide Cancelled - " + guide.getGuideNumber(),
                    "Your guide has been cancelled. Penalty amount: $" + penaltyAmount,
                    guide.getGuideNumber());

            // Notify courier if assigned
            if (guide.getCourier() != null) {
                notificationService.sendCancellationNotification(
                        guide.getCourier().getEmail(),
                        "Guide Cancelled - " + guide.getGuideNumber(),
                        "A guide assigned to you has been cancelled.",
                        guide.getGuideNumber());
            }
        } catch (Exception e) {
            // Log error but don't fail the transaction
        }

        return mapToCancellationResponseDto(savedCancellation);
    }

    private BigDecimal calculatePenaltyAmount(TrackingGuide guide, Business business) {
        LoyaltyLevel loyaltyLevel = business.getCurrentLevel();
        if (loyaltyLevel == null) {
            return guide.getBasePrice(); // 100% penalty if no loyalty level
        }

        // Check if business has free cancellations remaining
        // For simplicity, we'll apply the penalty percentage directly
        BigDecimal penaltyPercentage = loyaltyLevel.getPenaltyPercentage();
        return guide.getBasePrice().multiply(penaltyPercentage).divide(BigDecimal.valueOf(100));
    }

    private CancellationResponseDto mapToCancellationResponseDto(Cancellation cancellation) {
        return CancellationResponseDto.builder()
                .cancellation_id(cancellation.getCancellationId())
                .guide_id(cancellation.getGuide().getGuideId())
                .guide_number(cancellation.getGuide().getGuideNumber())
                .cancelled_by_user_id(cancellation.getCancelledByUser().getUserId())
                .cancelled_by_user_name(cancellation.getCancelledByUser().getFullName())
                .cancellation_type_id(cancellation.getCancellationType().getCancellationTypeId())
                .cancellation_type_name(cancellation.getCancellationType().getTypeName())
                .reason(cancellation.getReason())
                .penalty_amount(cancellation.getPenaltyAmount())
                .courier_commission(cancellation.getCourierCommission())
                .cancelled_at(cancellation.getCancelledAt())
                .build();
    }
}
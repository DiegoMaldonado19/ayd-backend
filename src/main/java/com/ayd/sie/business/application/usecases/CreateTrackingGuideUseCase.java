package com.ayd.sie.business.application.usecases;

import com.ayd.sie.business.application.dto.CreateGuideDto;
import com.ayd.sie.business.application.dto.GuideResponseDto;
import com.ayd.sie.shared.domain.entities.*;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.*;
import com.ayd.sie.shared.domain.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CreateTrackingGuideUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final BusinessJpaRepository businessRepository;
    private final BranchJpaRepository branchRepository;
    private final TrackingStateJpaRepository trackingStateRepository;
    private final NotificationService notificationService;

    @Transactional
    public GuideResponseDto execute(CreateGuideDto dto, Integer businessId) {
        // Validate business exists and is active
        Business business = businessRepository.findByUserUserIdAndActiveTrue(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        // Validate branch exists
        Branch branch = branchRepository.findById(dto.getOrigin_branch_id())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        // Get initial state (Creada)
        TrackingState initialState = trackingStateRepository.findByStateName("Creada")
                .orElseThrow(() -> new ResourceNotFoundException("Initial tracking state not found"));

        // Generate unique guide number
        String guideNumber = generateUniqueGuideNumber();

        // Apply loyalty discount if applicable
        BigDecimal finalPrice = applyLoyaltyDiscount(dto.getBase_price(), business);

        // Create tracking guide
        TrackingGuide guide = TrackingGuide.builder()
                .business(business)
                .originBranch(branch)
                .currentState(initialState)
                .guideNumber(guideNumber)
                .basePrice(finalPrice)
                .recipientName(dto.getRecipient_name())
                .recipientPhone(dto.getRecipient_phone())
                .recipientAddress(dto.getRecipient_address())
                .recipientCity(dto.getRecipient_city())
                .recipientState(dto.getRecipient_state())
                .observations(dto.getObservations())
                .build();

        TrackingGuide savedGuide = trackingGuideRepository.save(guide);

        // Send notification to business
        try {
            notificationService.sendBusinessNotification(
                    business.getEmail(),
                    "Guide Created - " + savedGuide.getGuideNumber(),
                    "Your tracking guide has been created successfully. Guide number: " + savedGuide.getGuideNumber());
        } catch (Exception e) {
            // Log error but don't fail the transaction
        }

        return mapToResponseDto(savedGuide);
    }

    private String generateUniqueGuideNumber() {
        String prefix = "GU";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.format("%04d", new Random().nextInt(10000));
        String guideNumber = prefix + timestamp + randomSuffix;

        // Ensure uniqueness
        while (trackingGuideRepository.findByGuideNumber(guideNumber).isPresent()) {
            randomSuffix = String.format("%04d", new Random().nextInt(10000));
            guideNumber = prefix + timestamp + randomSuffix;
        }

        return guideNumber;
    }

    private BigDecimal applyLoyaltyDiscount(BigDecimal basePrice, Business business) {
        if (business.getCurrentLevel() != null && business.getCurrentLevel().getDiscountPercentage() != null) {
            BigDecimal discountPercentage = business.getCurrentLevel().getDiscountPercentage();
            BigDecimal discount = basePrice.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
            return basePrice.subtract(discount);
        }
        return basePrice;
    }

    private GuideResponseDto mapToResponseDto(TrackingGuide guide) {
        return GuideResponseDto.builder()
                .guide_id(guide.getGuideId())
                .guide_number(guide.getGuideNumber())
                .business_id(guide.getBusiness().getBusinessId())
                .business_name(guide.getBusiness().getBusinessName())
                .origin_branch_id(guide.getOriginBranch().getBranchId())
                .origin_branch_name(guide.getOriginBranch().getBranchName())
                .courier_id(guide.getCourier() != null ? guide.getCourier().getUserId() : null)
                .courier_name(guide.getCourier() != null ? guide.getCourier().getFullName() : null)
                .coordinator_id(guide.getCoordinator() != null ? guide.getCoordinator().getUserId() : null)
                .coordinator_name(guide.getCoordinator() != null ? guide.getCoordinator().getFullName() : null)
                .current_state_id(guide.getCurrentState().getStateId())
                .current_state_name(guide.getCurrentState().getStateName())
                .current_state_description(guide.getCurrentState().getDescription())
                .state_is_final(guide.getCurrentState().getIsFinal())
                .base_price(guide.getBasePrice())
                .courier_commission(guide.getCourierCommission())
                .recipient_name(guide.getRecipientName())
                .recipient_phone(guide.getRecipientPhone())
                .recipient_address(guide.getRecipientAddress())
                .recipient_city(guide.getRecipientCity())
                .recipient_state(guide.getRecipientState())
                .observations(guide.getObservations())
                .assignment_accepted(guide.getAssignmentAccepted())
                .assignment_accepted_at(guide.getAssignmentAcceptedAt())
                .created_at(guide.getCreatedAt())
                .updated_at(guide.getUpdatedAt())
                .assignment_date(guide.getAssignmentDate())
                .pickup_date(guide.getPickupDate())
                .delivery_date(guide.getDeliveryDate())
                .cancellation_date(guide.getCancellationDate())
                .build();
    }
}
package com.ayd.sie.business.application.usecases;

import com.ayd.sie.business.application.dto.GuideResponseDto;
import com.ayd.sie.business.application.dto.UpdateGuideDto;
import com.ayd.sie.shared.domain.entities.Business;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.domain.exceptions.BusinessConstraintViolationException;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.BusinessJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UpdateTrackingGuideUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final BusinessJpaRepository businessRepository;

    @Transactional
    public GuideResponseDto execute(Integer guideId, UpdateGuideDto dto, Integer businessId) {
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

        // Validate guide can be edited (only before pickup)
        if (guide.getPickupDate() != null) {
            throw new BusinessConstraintViolationException("Cannot edit guide after pickup");
        }

        // Apply loyalty discount to new price
        BigDecimal finalPrice = applyLoyaltyDiscount(dto.getBase_price(), business);

        // Update guide fields
        guide.setBasePrice(finalPrice);
        guide.setRecipientName(dto.getRecipient_name());
        guide.setRecipientPhone(dto.getRecipient_phone());
        guide.setRecipientAddress(dto.getRecipient_address());
        guide.setRecipientCity(dto.getRecipient_city());
        guide.setRecipientState(dto.getRecipient_state());
        guide.setObservations(dto.getObservations());

        TrackingGuide updatedGuide = trackingGuideRepository.save(guide);

        return mapToResponseDto(updatedGuide);
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
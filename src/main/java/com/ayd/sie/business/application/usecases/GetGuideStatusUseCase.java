package com.ayd.sie.business.application.usecases;

import com.ayd.sie.business.application.dto.GuideStatusDto;
import com.ayd.sie.shared.domain.entities.Business;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.domain.exceptions.BusinessConstraintViolationException;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.BusinessJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetGuideStatusUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final BusinessJpaRepository businessRepository;

    public GuideStatusDto execute(Integer guideId, Integer businessId) {
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

        return mapToStatusDto(guide);
    }

    public GuideStatusDto executeByGuideNumber(String guideNumber, Integer businessId) {
        // Validate business exists and is active
        Business business = businessRepository.findByUserUserIdAndActiveTrue(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        // Find guide by guide number
        TrackingGuide guide = trackingGuideRepository.findByGuideNumber(guideNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Guide not found"));

        // Validate guide belongs to business
        if (!guide.getBusiness().getBusinessId().equals(business.getBusinessId())) {
            throw new BusinessConstraintViolationException("Guide does not belong to the business");
        }

        return mapToStatusDto(guide);
    }

    private GuideStatusDto mapToStatusDto(TrackingGuide guide) {
        return GuideStatusDto.builder()
                .guide_id(guide.getGuideId())
                .guide_number(guide.getGuideNumber())
                .current_state_id(guide.getCurrentState().getStateId())
                .current_state_name(guide.getCurrentState().getStateName())
                .current_state_description(guide.getCurrentState().getDescription())
                .state_is_final(guide.getCurrentState().getIsFinal())
                .recipient_name(guide.getRecipientName())
                .recipient_city(guide.getRecipientCity())
                .recipient_state(guide.getRecipientState())
                .created_at(guide.getCreatedAt())
                .assignment_date(guide.getAssignmentDate())
                .pickup_date(guide.getPickupDate())
                .delivery_date(guide.getDeliveryDate())
                .cancellation_date(guide.getCancellationDate())
                .courier_name(guide.getCourier() != null ? guide.getCourier().getFullName() : null)
                .courier_phone(guide.getCourier() != null ? guide.getCourier().getPhone() : null)
                .build();
    }
}
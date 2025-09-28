package com.ayd.sie.business.application.usecases;

import com.ayd.sie.business.application.dto.LoyaltyBenefitsDto;
import com.ayd.sie.shared.domain.entities.Business;
import com.ayd.sie.shared.domain.entities.LoyaltyLevel;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.BusinessJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetLoyaltyBenefitsUseCase {

    private final BusinessJpaRepository businessRepository;
    private final TrackingGuideJpaRepository trackingGuideRepository;

    public LoyaltyBenefitsDto execute(Integer businessId) {
        // Validate business exists and is active
        Business business = businessRepository.findByUserUserIdAndActiveTrue(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        LoyaltyLevel currentLevel = business.getCurrentLevel();
        if (currentLevel == null) {
            throw new ResourceNotFoundException("Business has no loyalty level assigned");
        }

        // Count current deliveries for this business
        long currentDeliveries = trackingGuideRepository.countByBusinessId(business.getBusinessId());

        // Count cancellations made by business this month/period
        // For simplicity, we'll assume all free cancellations are available
        int remainingFreeCancellations = currentLevel.getFreeCancellations() != null
                ? currentLevel.getFreeCancellations()
                : 0;

        return LoyaltyBenefitsDto.builder()
                .level_id(currentLevel.getLevelId())
                .level_name(currentLevel.getLevelName())
                .min_deliveries(currentLevel.getMinDeliveries())
                .max_deliveries(currentLevel.getMaxDeliveries())
                .discount_percentage(currentLevel.getDiscountPercentage())
                .free_cancellations(currentLevel.getFreeCancellations())
                .penalty_percentage(currentLevel.getPenaltyPercentage())
                .current_deliveries((int) currentDeliveries)
                .remaining_free_cancellations(remainingFreeCancellations)
                .active(currentLevel.getActive())
                .build();
    }
}
package com.ayd.sie.business.application.services;

import com.ayd.sie.business.application.dto.*;
import com.ayd.sie.business.application.usecases.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Business Application Service
 * Coordinates business-related use cases for affiliated commerce operations
 */
@Service
@RequiredArgsConstructor
public class BusinessApplicationService {

    private final CreateTrackingGuideUseCase createTrackingGuideUseCase;
    private final UpdateTrackingGuideUseCase updateTrackingGuideUseCase;
    private final GetGuideStatusUseCase getGuideStatusUseCase;
    private final CancelDeliveryUseCase cancelDeliveryUseCase;
    private final GetLoyaltyBenefitsUseCase getLoyaltyBenefitsUseCase;
    private final GetBusinessGuidesUseCase getBusinessGuidesUseCase;

    /**
     * Creates a new tracking guide with automatic loyalty discount application
     */
    public GuideResponseDto createGuide(CreateGuideDto createGuideDto, Integer businessId) {
        return createTrackingGuideUseCase.execute(createGuideDto, businessId);
    }

    /**
     * Updates a tracking guide (only before pickup)
     */
    public GuideResponseDto updateGuide(Integer guideId, UpdateGuideDto updateGuideDto, Integer businessId) {
        return updateTrackingGuideUseCase.execute(guideId, updateGuideDto, businessId);
    }

    /**
     * Gets guide status by ID
     */
    public GuideStatusDto getGuideStatus(Integer guideId, Integer businessId) {
        return getGuideStatusUseCase.execute(guideId, businessId);
    }

    /**
     * Gets guide status by guide number
     */
    public GuideStatusDto getGuideStatusByNumber(String guideNumber, Integer businessId) {
        return getGuideStatusUseCase.executeByGuideNumber(guideNumber, businessId);
    }

    /**
     * Lists business guides with pagination
     */
    public GuideListResponseDto getBusinessGuides(Integer businessId, Pageable pageable) {
        return getBusinessGuidesUseCase.execute(businessId, pageable);
    }

    /**
     * Lists active business guides with pagination
     */
    public GuideListResponseDto getActiveBusinessGuides(Integer businessId, Pageable pageable) {
        return getBusinessGuidesUseCase.executeActiveOnly(businessId, pageable);
    }

    /**
     * Cancels a delivery with penalty calculation based on loyalty level
     */
    public CancellationResponseDto cancelDelivery(Integer guideId, CancelGuideDto cancelGuideDto, Integer businessId) {
        return cancelDeliveryUseCase.execute(guideId, cancelGuideDto, businessId);
    }

    /**
     * Gets current loyalty benefits and status
     */
    public LoyaltyBenefitsDto getLoyaltyBenefits(Integer businessId) {
        return getLoyaltyBenefitsUseCase.execute(businessId);
    }
}
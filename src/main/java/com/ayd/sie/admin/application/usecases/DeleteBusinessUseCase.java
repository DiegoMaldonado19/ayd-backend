package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.Business;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.BusinessJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteBusinessUseCase {

    private final BusinessJpaRepository businessRepository;
    private final TrackingGuideJpaRepository trackingGuideRepository;

    @Transactional
    public void execute(Integer businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new InvalidCredentialsException("Business not found"));

        validateBusinessCanBeDeleted(businessId, business.getBusinessName());

        businessRepository.delete(business);
        log.info("Business permanently deleted with ID: {} and name: {}", businessId, business.getBusinessName());
    }

    private void validateBusinessCanBeDeleted(Integer businessId, String businessName) {
        long totalGuides = trackingGuideRepository.countByBusinessId(businessId);
        if (totalGuides > 0) {
            long activeGuides = trackingGuideRepository.countActiveByBusinessId(businessId);
            throw new ResourceNotFoundException(
                    String.format(
                            "Cannot delete business '%s'. It has %d tracking guides associated (%d active, %d completed/cancelled).",
                            businessName, totalGuides, activeGuides, totalGuides - activeGuides));
        }
    }
}
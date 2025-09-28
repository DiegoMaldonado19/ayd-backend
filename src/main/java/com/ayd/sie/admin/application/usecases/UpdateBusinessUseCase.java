package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.BusinessDto;
import com.ayd.sie.admin.application.dto.UpdateBusinessRequestDto;
import com.ayd.sie.shared.domain.entities.Business;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.BusinessJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateBusinessUseCase {

    private final BusinessJpaRepository businessRepository;

    @Transactional
    public BusinessDto execute(Integer businessId, UpdateBusinessRequestDto request) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        if (!Boolean.TRUE.equals(business.getActive())) {
            throw new ResourceNotFoundException("Cannot update inactive business");
        }

        business.setBusinessName(request.getBusinessName());
        business.setLegalName(request.getLegalName());
        business.setTaxAddress(request.getTaxAddress());
        business.setBusinessPhone(request.getBusinessPhone());
        business.setBusinessEmail(request.getBusinessEmail());
        business.setSupportContact(request.getSupportContact());

        // Update active status if provided
        if (request.getActive() != null) {
            business.setActive(request.getActive());
        }

        Business savedBusiness = businessRepository.save(business);

        log.info("Business updated successfully with ID: {}", businessId);

        return mapToDto(savedBusiness);
    }

    private BusinessDto mapToDto(Business business) {
        return BusinessDto.builder()
                .businessId(business.getBusinessId())
                .userId(business.getUser().getUserId())
                .userEmail(business.getUser().getEmail())
                .userFullName(business.getUser().getFullName())
                .currentLevelId(business.getCurrentLevel().getLevelId())
                .currentLevelName(business.getCurrentLevel().getLevelName())
                .taxId(business.getTaxId())
                .businessName(business.getBusinessName())
                .legalName(business.getLegalName())
                .taxAddress(business.getTaxAddress())
                .businessPhone(business.getBusinessPhone())
                .businessEmail(business.getBusinessEmail())
                .supportContact(business.getSupportContact())
                .active(business.getActive())
                .affiliationDate(business.getAffiliationDate())
                .createdAt(business.getCreatedAt())
                .updatedAt(business.getUpdatedAt())
                .build();
    }
}
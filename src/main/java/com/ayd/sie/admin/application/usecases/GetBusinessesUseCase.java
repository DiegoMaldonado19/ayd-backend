package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.BusinessDto;
import com.ayd.sie.shared.domain.entities.Business;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.BusinessJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetBusinessesUseCase {

    private final BusinessJpaRepository businessRepository;

    @Transactional(readOnly = true)
    public Page<BusinessDto> execute(String search, Pageable pageable) {
        Page<Business> businesses;

        if (search != null && !search.trim().isEmpty()) {
            businesses = businessRepository.findActiveBySearch(search.trim(), pageable);
        } else {
            businesses = businessRepository.findByActiveTrue(pageable);
        }

        return businesses.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public BusinessDto findById(Integer businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));
        return mapToDto(business);
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
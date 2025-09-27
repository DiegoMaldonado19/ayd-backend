package com.ayd.sie.admin.application.usecases;

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
public class ActivateBusinessUseCase {

    private final BusinessJpaRepository businessRepository;

    @Transactional
    public void execute(Integer businessId, boolean active) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        business.setActive(active);
        business.getUser().setActive(active);
        businessRepository.save(business);

        log.info("Business {} status changed to: {}", businessId, active ? "ACTIVE" : "INACTIVE");
    }
}
package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.Business;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.BusinessJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuspendBusinessUseCase {

    private final BusinessJpaRepository businessRepository;

    @Transactional
    public void execute(Integer businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new InvalidCredentialsException("Business not found"));

        if (!Boolean.TRUE.equals(business.getActive())) {
            throw new InvalidCredentialsException("Business is already suspended");
        }

        business.setActive(false);
        business.getUser().setActive(false);
        businessRepository.save(business);

        log.info("Business suspended successfully with ID: {}", businessId);
    }
}
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
public class DeleteBusinessUseCase {

    private final BusinessJpaRepository businessRepository;

    @Transactional
    public void execute(Integer businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new InvalidCredentialsException("Business not found"));

        // Note: Business deletion validation would need tracking guides entity
        // For now, we'll allow deletion and rely on database constraints

        businessRepository.delete(business);
        log.info("Business permanently deleted with ID: {}", businessId);
    }
}
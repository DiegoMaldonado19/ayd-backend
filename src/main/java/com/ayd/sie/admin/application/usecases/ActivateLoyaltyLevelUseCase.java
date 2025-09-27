package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.LoyaltyLevel;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.LoyaltyLevelJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivateLoyaltyLevelUseCase {

    private final LoyaltyLevelJpaRepository loyaltyLevelRepository;

    @Transactional
    public void execute(Integer levelId, boolean active) {
        LoyaltyLevel loyaltyLevel = loyaltyLevelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Loyalty level not found"));

        loyaltyLevel.setActive(active);
        loyaltyLevelRepository.save(loyaltyLevel);

        log.info("Loyalty level {} status changed to: {}", levelId, active ? "ACTIVE" : "INACTIVE");
    }
}
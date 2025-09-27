package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.LoyaltyLevel;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.BusinessJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.LoyaltyLevelJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteLoyaltyLevelUseCase {

    private final LoyaltyLevelJpaRepository loyaltyLevelRepository;
    private final BusinessJpaRepository businessRepository;

    @Transactional
    public void execute(Integer levelId) {
        LoyaltyLevel loyaltyLevel = loyaltyLevelRepository.findById(levelId)
                .orElseThrow(() -> new InvalidCredentialsException("Loyalty level not found"));

        // Check if loyalty level has active businesses
        boolean hasActiveBusinesses = !businessRepository.findByLoyaltyLevel(levelId).isEmpty();
        if (hasActiveBusinesses) {
            throw new InvalidCredentialsException("Cannot delete loyalty level with active businesses");
        }

        loyaltyLevelRepository.delete(loyaltyLevel);
        log.info("Loyalty level permanently deleted with ID: {}", levelId);
    }
}
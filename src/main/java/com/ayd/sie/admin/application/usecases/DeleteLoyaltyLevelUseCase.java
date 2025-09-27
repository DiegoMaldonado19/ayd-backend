package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.LoyaltyLevel;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.domain.exceptions.ResourceHasDependenciesException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Loyalty level not found"));

        // Count all businesses with this loyalty level
        long totalBusinesses = businessRepository.countByCurrentLevelLevelId(levelId);

        log.info("Loyalty level {} has {} businesses associated", levelId, totalBusinesses);

        if (totalBusinesses > 0) {
            String errorMessage = String.format(
                    "Cannot delete loyalty level '%s' (ID: %d). It has %d business(es) associated. " +
                            "Please reassign businesses to another loyalty level before deleting.",
                    loyaltyLevel.getLevelName(), levelId, totalBusinesses);
            log.error(errorMessage);
            throw new ResourceHasDependenciesException(errorMessage);
        }

        loyaltyLevelRepository.delete(loyaltyLevel);
        log.info("Loyalty level '{}' permanently deleted with ID: {}", loyaltyLevel.getLevelName(), levelId);
    }
}
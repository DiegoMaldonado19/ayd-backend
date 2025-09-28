package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.UpdateLoyaltyLevelRequestDto;
import com.ayd.sie.admin.application.dto.LoyaltyLevelDto;
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
public class UpdateLoyaltyLevelUseCase {

    private final LoyaltyLevelJpaRepository loyaltyLevelRepository;

    @Transactional
    public LoyaltyLevelDto execute(Integer levelId, UpdateLoyaltyLevelRequestDto request) {
        LoyaltyLevel existingLevel = loyaltyLevelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Loyalty level not found with ID: " + levelId));

        // Validate that the level name is not already taken by another level
        if (loyaltyLevelRepository.existsByLevelNameAndActiveTrueAndLevelIdNot(
                request.getLevelName(), levelId)) {
            throw new ResourceNotFoundException("Loyalty level name already exists");
        }

        // Validate delivery range
        if (request.getMaxDeliveries() != null &&
                request.getMaxDeliveries() <= request.getMinDeliveries()) {
            throw new ResourceNotFoundException("Maximum deliveries must be greater than minimum deliveries");
        }

        // Update the existing loyalty level
        existingLevel.setLevelName(request.getLevelName());
        existingLevel.setMinDeliveries(request.getMinDeliveries());
        existingLevel.setMaxDeliveries(request.getMaxDeliveries());
        existingLevel.setDiscountPercentage(request.getDiscountPercentage());
        existingLevel.setFreeCancellations(request.getFreeCancellations());
        existingLevel.setPenaltyPercentage(request.getPenaltyPercentage());

        LoyaltyLevel updatedLevel = loyaltyLevelRepository.save(existingLevel);

        log.info("Loyalty level updated successfully: {} (ID: {})", updatedLevel.getLevelName(),
                updatedLevel.getLevelId());

        return mapToDto(updatedLevel);
    }

    private LoyaltyLevelDto mapToDto(LoyaltyLevel level) {
        return LoyaltyLevelDto.builder()
                .levelId(level.getLevelId())
                .levelName(level.getLevelName())
                .minDeliveries(level.getMinDeliveries())
                .maxDeliveries(level.getMaxDeliveries())
                .discountPercentage(level.getDiscountPercentage())
                .freeCancellations(level.getFreeCancellations())
                .penaltyPercentage(level.getPenaltyPercentage())
                .active(level.getActive())
                .createdAt(level.getCreatedAt())
                .updatedAt(level.getUpdatedAt())
                .build();
    }
}
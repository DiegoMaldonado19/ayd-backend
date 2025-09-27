package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.CreateLoyaltyLevelRequestDto;
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
public class CreateLoyaltyLevelUseCase {

    private final LoyaltyLevelJpaRepository loyaltyLevelRepository;

    @Transactional
    public LoyaltyLevelDto execute(CreateLoyaltyLevelRequestDto request) {
        if (loyaltyLevelRepository.existsByLevelNameAndActiveTrue(request.getLevelName())) {
            throw new ResourceNotFoundException("Loyalty level name already exists");
        }

        if (request.getMaxDeliveries() != null &&
                request.getMaxDeliveries() <= request.getMinDeliveries()) {
            throw new ResourceNotFoundException("Maximum deliveries must be greater than minimum deliveries");
        }

        LoyaltyLevel loyaltyLevel = LoyaltyLevel.builder()
                .levelName(request.getLevelName())
                .minDeliveries(request.getMinDeliveries())
                .maxDeliveries(request.getMaxDeliveries())
                .discountPercentage(request.getDiscountPercentage())
                .freeCancellations(request.getFreeCancellations())
                .penaltyPercentage(request.getPenaltyPercentage())
                .active(true)
                .build();

        LoyaltyLevel savedLevel = loyaltyLevelRepository.save(loyaltyLevel);

        log.info("Loyalty level created successfully: {}", savedLevel.getLevelName());

        return mapToDto(savedLevel);
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
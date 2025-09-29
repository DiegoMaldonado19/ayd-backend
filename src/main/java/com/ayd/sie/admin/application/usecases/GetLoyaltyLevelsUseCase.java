package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.LoyaltyLevelDto;
import com.ayd.sie.shared.domain.entities.LoyaltyLevel;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.LoyaltyLevelJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetLoyaltyLevelsUseCase {

    private final LoyaltyLevelJpaRepository loyaltyLevelRepository;

    @Transactional(readOnly = true)
    public List<LoyaltyLevelDto> execute() {
        List<LoyaltyLevel> levels = loyaltyLevelRepository.findAllByOrderByMinDeliveries();
        return levels.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LoyaltyLevelDto findById(Integer levelId) {
        LoyaltyLevel level = loyaltyLevelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Loyalty level not found with id: " + levelId));
        return mapToDto(level);
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

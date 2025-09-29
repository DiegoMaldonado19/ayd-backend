package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.CancellationTypeDto;
import com.ayd.sie.shared.infrastructure.persistence.CancellationTypeJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetCancellationTypesUseCase {

    private final CancellationTypeJpaRepository cancellationTypeRepository;

    @Transactional(readOnly = true)
    public List<CancellationTypeDto> execute() {
        return cancellationTypeRepository.findByActiveTrueOrderByTypeName()
                .stream()
                .map(cancellationType -> CancellationTypeDto.builder()
                        .cancellationTypeId(cancellationType.getCancellationTypeId())
                        .typeName(cancellationType.getTypeName())
                        .description(cancellationType.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}

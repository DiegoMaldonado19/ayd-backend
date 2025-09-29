package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.IncidentTypeDto;
import com.ayd.sie.shared.infrastructure.persistence.IncidentTypeJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetIncidentTypesUseCase {

    private final IncidentTypeJpaRepository incidentTypeRepository;

    @Transactional(readOnly = true)
    public List<IncidentTypeDto> execute() {
        return incidentTypeRepository.findAllActive()
                .stream()
                .map(incidentType -> IncidentTypeDto.builder()
                        .incidentTypeId(incidentType.getIncidentTypeId())
                        .typeName(incidentType.getTypeName())
                        .description(incidentType.getDescription())
                        .requiresReturn(incidentType.getRequiresReturn())
                        .build())
                .collect(Collectors.toList());
    }
}

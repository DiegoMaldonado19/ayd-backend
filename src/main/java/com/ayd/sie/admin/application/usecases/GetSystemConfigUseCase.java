package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.SystemConfigDto;
import com.ayd.sie.shared.domain.entities.SystemConfig;
import com.ayd.sie.shared.infrastructure.persistence.SystemConfigJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetSystemConfigUseCase {

    private final SystemConfigJpaRepository systemConfigRepository;

    @Transactional(readOnly = true)
    public List<SystemConfigDto> execute() {
        List<SystemConfig> configs = systemConfigRepository.findAllByOrderByConfigKey();
        return configs.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private SystemConfigDto mapToDto(SystemConfig config) {
        return SystemConfigDto.builder()
                .configId(config.getConfigId())
                .configKey(config.getConfigKey())
                .configValue(config.getConfigValue())
                .description(config.getDescription())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
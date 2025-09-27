package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.SystemConfigDto;
import com.ayd.sie.admin.application.dto.UpdateSystemConfigRequestDto;
import com.ayd.sie.shared.domain.entities.SystemConfig;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.SystemConfigJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateSystemConfigUseCase {

    private final SystemConfigJpaRepository systemConfigRepository;

    @Transactional
    public SystemConfigDto execute(String configKey, UpdateSystemConfigRequestDto request) {
        SystemConfig config = systemConfigRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new InvalidCredentialsException("Configuration not found"));

        config.setConfigValue(request.getConfigValue());
        if (request.getDescription() != null) {
            config.setDescription(request.getDescription());
        }

        SystemConfig savedConfig = systemConfigRepository.save(config);

        log.info("System configuration updated: {}", configKey);

        return mapToDto(savedConfig);
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
package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.AuditLogDto;
import com.ayd.sie.shared.domain.entities.AuditLog;
import com.ayd.sie.shared.infrastructure.persistence.AuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAuditLogUseCase {

    private final AuditLogJpaRepository auditLogRepository;

    @Transactional(readOnly = true)
    public Page<AuditLogDto> execute(String tableName, Integer userId,
            LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable) {
        Page<AuditLog> auditLogs;

        if (startDate != null && endDate != null) {
            auditLogs = auditLogRepository.findByDateRange(startDate, endDate, pageable);
        } else if (tableName != null && !tableName.trim().isEmpty()) {
            auditLogs = auditLogRepository.findByTableName(tableName, pageable);
        } else if (userId != null) {
            auditLogs = auditLogRepository.findByUserId(userId, pageable);
        } else {
            auditLogs = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return auditLogs.map(this::mapToDto);
    }

    private AuditLogDto mapToDto(AuditLog auditLog) {
        return AuditLogDto.builder()
                .logId(auditLog.getLogId())
                .userId(auditLog.getUser() != null ? auditLog.getUser().getUserId() : null)
                .userFullName(auditLog.getUser() != null ? auditLog.getUser().getFullName() : "System")
                .tableName(auditLog.getTableName())
                .operationTypeId(auditLog.getOperationTypeId())
                .operationName(getOperationName(auditLog.getOperationTypeId()))
                .recordId(auditLog.getRecordId())
                .oldData(auditLog.getOldData())
                .newData(auditLog.getNewData())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }

    private String getOperationName(Integer operationTypeId) {
        return switch (operationTypeId) {
            case 1 -> "Insercion";
            case 2 -> "Actualizacion";
            case 3 -> "Eliminacion";
            default -> "Desconocido";
        };
    }
}
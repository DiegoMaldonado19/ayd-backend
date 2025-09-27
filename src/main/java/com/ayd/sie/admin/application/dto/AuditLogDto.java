package com.ayd.sie.admin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Audit log entry")
public class AuditLogDto {

    @JsonProperty("log_id")
    @Schema(description = "Log unique identifier")
    private Integer logId;

    @JsonProperty("user_id")
    @Schema(description = "User ID who performed the action")
    private Integer userId;

    @JsonProperty("user_full_name")
    @Schema(description = "User full name")
    private String userFullName;

    @JsonProperty("table_name")
    @Schema(description = "Table name affected")
    private String tableName;

    @JsonProperty("operation_type_id")
    @Schema(description = "Operation type ID")
    private Integer operationTypeId;

    @JsonProperty("operation_name")
    @Schema(description = "Operation name")
    private String operationName;

    @JsonProperty("record_id")
    @Schema(description = "Record ID affected")
    private Integer recordId;

    @JsonProperty("old_data")
    @Schema(description = "Old data (JSON)")
    private String oldData;

    @JsonProperty("new_data")
    @Schema(description = "New data (JSON)")
    private String newData;

    @JsonProperty("ip_address")
    @Schema(description = "IP address")
    private String ipAddress;

    @JsonProperty("user_agent")
    @Schema(description = "User agent")
    private String userAgent;

    @JsonProperty("created_at")
    @Schema(description = "Timestamp")
    private LocalDateTime createdAt;
}
package com.ayd.sie.shared.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponseDto {

    private boolean sent;
    private String messageId;
    private String errorMessage;
    private LocalDateTime sentAt;
}
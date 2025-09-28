package com.ayd.sie.business.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancellationResponseDto {

    private Integer cancellation_id;
    private Integer guide_id;
    private String guide_number;
    private Integer cancelled_by_user_id;
    private String cancelled_by_user_name;
    private Integer cancellation_type_id;
    private String cancellation_type_name;
    private String reason;
    private BigDecimal penalty_amount;
    private BigDecimal courier_commission;
    private LocalDateTime cancelled_at;
}
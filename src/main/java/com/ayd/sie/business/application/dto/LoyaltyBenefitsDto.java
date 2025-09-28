package com.ayd.sie.business.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyBenefitsDto {

    private Integer level_id;
    private String level_name;
    private Integer min_deliveries;
    private Integer max_deliveries;
    private BigDecimal discount_percentage;
    private Integer free_cancellations;
    private BigDecimal penalty_percentage;
    private Integer current_deliveries;
    private Integer remaining_free_cancellations;
    private Boolean active;
}
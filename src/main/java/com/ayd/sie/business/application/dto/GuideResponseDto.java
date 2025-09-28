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
public class GuideResponseDto {

    private Integer guide_id;
    private String guide_number;
    private Integer business_id;
    private String business_name;
    private Integer origin_branch_id;
    private String origin_branch_name;
    private Integer courier_id;
    private String courier_name;
    private Integer coordinator_id;
    private String coordinator_name;
    private Integer current_state_id;
    private String current_state_name;
    private String current_state_description;
    private Boolean state_is_final;
    private BigDecimal base_price;
    private BigDecimal courier_commission;
    private String recipient_name;
    private String recipient_phone;
    private String recipient_address;
    private String recipient_city;
    private String recipient_state;
    private String observations;
    private Boolean assignment_accepted;
    private LocalDateTime assignment_accepted_at;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime assignment_date;
    private LocalDateTime pickup_date;
    private LocalDateTime delivery_date;
    private LocalDateTime cancellation_date;
}
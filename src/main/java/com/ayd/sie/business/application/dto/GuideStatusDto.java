package com.ayd.sie.business.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideStatusDto {

    private Integer guide_id;
    private String guide_number;
    private Integer current_state_id;
    private String current_state_name;
    private String current_state_description;
    private Boolean state_is_final;
    private String recipient_name;
    private String recipient_city;
    private String recipient_state;
    private LocalDateTime created_at;
    private LocalDateTime assignment_date;
    private LocalDateTime pickup_date;
    private LocalDateTime delivery_date;
    private LocalDateTime cancellation_date;
    private String courier_name;
    private String courier_phone;
}
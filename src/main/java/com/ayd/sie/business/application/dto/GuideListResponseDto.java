package com.ayd.sie.business.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideListResponseDto {

    private List<GuideResponseDto> guides;
    private int total_count;
    private int page;
    private int size;
    private boolean has_next;
    private boolean has_previous;
}
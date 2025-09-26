package com.ayd.sie.shared.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDto {

    private String to;
    private String subject;
    private String templateName;
    private Map<String, Object> variables;
    private boolean isHtml;
}
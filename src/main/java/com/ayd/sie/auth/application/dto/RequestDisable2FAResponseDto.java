package com.ayd.sie.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request disable 2FA response")
public class RequestDisable2FAResponseDto {

    @JsonProperty("code_sent")
    @Schema(description = "Indicates if verification code was sent")
    private Boolean codeSent;

    @JsonProperty("message")
    @Schema(description = "Status message")
    private String message;
}
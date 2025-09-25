package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.ValidateResetTokenRequestDto;
import com.ayd.sie.auth.application.dto.ValidateResetTokenResponseDto;
import com.ayd.sie.shared.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidateResetTokenUseCase {

    private final JwtUtil jwtUtil;

    public ValidateResetTokenResponseDto execute(ValidateResetTokenRequestDto request) {
        try {
            String email = jwtUtil.extractUsername(request.getResetToken());
            String tokenType = jwtUtil.extractClaim(request.getResetToken(), 
                claims -> claims.get("tokenType", String.class));
            
            if (!"PASSWORD_RESET".equals(tokenType)) {
                return ValidateResetTokenResponseDto.builder()
                        .isValid(false)
                        .build();
            }

            Date expiration = jwtUtil.extractExpiration(request.getResetToken());
            long expiresInMillis = expiration.getTime() - System.currentTimeMillis();
            
            if (expiresInMillis <= 0) {
                return ValidateResetTokenResponseDto.builder()
                        .isValid(false)
                        .build();
            }

            return ValidateResetTokenResponseDto.builder()
                    .isValid(true)
                    .expiresInMinutes(expiresInMillis / 60000)
                    .email(email)
                    .build();

        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return ValidateResetTokenResponseDto.builder()
                    .isValid(false)
                    .build();
        }
    }
}
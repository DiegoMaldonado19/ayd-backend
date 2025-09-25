package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.ValidateTokenRequestDto;
import com.ayd.sie.auth.application.dto.ValidateTokenResponseDto;
import com.ayd.sie.shared.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidateTokenUseCase {

    private final JwtUtil jwtUtil;

    public ValidateTokenResponseDto execute(ValidateTokenRequestDto request) {
        try {
            String email = jwtUtil.extractUsername(request.getToken());
            String role = jwtUtil.extractRole(request.getToken());
            Date expiration = jwtUtil.extractExpiration(request.getToken());

            boolean isValid = jwtUtil.validateToken(request.getToken(), email);

            LocalDateTime expiresAt = expiration.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            return ValidateTokenResponseDto.builder()
                    .isValid(isValid)
                    .expiresAt(expiresAt)
                    .userEmail(email)
                    .userRole(role)
                    .build();

        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return ValidateTokenResponseDto.builder()
                    .isValid(false)
                    .build();
        }
    }
}
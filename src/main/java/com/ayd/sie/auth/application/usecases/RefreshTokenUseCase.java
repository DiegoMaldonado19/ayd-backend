package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.RefreshTokenRequestDto;
import com.ayd.sie.auth.application.dto.RefreshTokenResponseDto;
import com.ayd.sie.config.AppProperties;
import com.ayd.sie.shared.domain.entities.RefreshToken;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidTokenException;
import com.ayd.sie.shared.infrastructure.persistence.RefreshTokenJpaRepository;
import com.ayd.sie.shared.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenUseCase {

    private final RefreshTokenJpaRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final AppProperties appProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public RefreshTokenResponseDto execute(RefreshTokenRequestDto request, String ipAddress, String userAgent) {
        String tokenHash = hashToken(request.getRefreshToken());

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (!refreshToken.isValid()) {
            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);
            throw new InvalidTokenException("Refresh token has expired or been revoked");
        }

        User user = refreshToken.getUser();
        if (!user.isEnabled()) {
            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);
            throw new InvalidTokenException("User account is disabled");
        }

        // Generate new access token
        String newAccessToken = jwtUtil.generateToken(
                user.getEmail(),
                user.getUserId(),
                user.getRole().getRoleName());

        // Optionally generate new refresh token for rotation
        RefreshToken newRefreshToken = rotateRefreshToken(refreshToken, ipAddress, userAgent);

        log.info("Token refreshed for user: {}", user.getEmail());

        return RefreshTokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getTokenHash())
                .expiresIn(appProperties.getJwt().getExpiration() / 1000)
                .build();
    }

    private RefreshToken rotateRefreshToken(RefreshToken oldToken, String ipAddress, String userAgent) {
        // Revoke old token
        oldToken.revoke();
        refreshTokenRepository.save(oldToken);

        // Create new token
        String tokenValue = generateSecureToken();
        String tokenHash = hashToken(tokenValue);

        RefreshToken newToken = RefreshToken.builder()
                .user(oldToken.getUser())
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(appProperties.getJwt().getRefreshExpiration())))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        return refreshTokenRepository.save(newToken);
    }

    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
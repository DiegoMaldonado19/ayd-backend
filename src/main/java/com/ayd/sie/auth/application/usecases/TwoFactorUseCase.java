package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.LoginResponseDto;
import com.ayd.sie.auth.application.dto.TwoFactorRequestDto;
import com.ayd.sie.config.AppProperties;
import com.ayd.sie.shared.domain.entities.RefreshToken;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.RefreshTokenJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
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
public class TwoFactorUseCase {

    private final UserJpaRepository userRepository;
    private final RefreshTokenJpaRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final AppProperties appProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public LoginResponseDto execute(TwoFactorRequestDto request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmailAndActiveTrue(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid user"));

        if (!user.isTwoFactorCodeValid(request.getVerificationCode())) {
            log.warn("Invalid 2FA code for user: {}", user.getEmail());
            throw new InvalidCredentialsException("Invalid or expired verification code");
        }

        // Clear 2FA code
        user.setTwoFactorCode(null);
        user.setTwoFactorExpiration(null);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtUtil.generateToken(
                user.getEmail(),
                user.getUserId(),
                user.getRole().getRoleName());

        RefreshToken refreshToken = createRefreshToken(user, ipAddress, userAgent);

        log.info("2FA authentication successful for user: {}", user.getEmail());

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getTokenHash())
                .expiresIn(appProperties.getJwt().getExpiration() / 1000)
                .userInfo(buildUserInfo(user))
                .twoFactorRequired(false)
                .build();
    }

    private RefreshToken createRefreshToken(User user, String ipAddress, String userAgent) {
        refreshTokenRepository.revokeAllUserTokens(user, LocalDateTime.now());

        String tokenValue = generateSecureToken();
        String tokenHash = hashToken(tokenValue);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(appProperties.getJwt().getRefreshExpiration())))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        return refreshTokenRepository.save(refreshToken);
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

    private LoginResponseDto.UserInfoDto buildUserInfo(User user) {
        return LoginResponseDto.UserInfoDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole().getRoleName())
                .twoFactorEnabled(user.isTwoFactorRequired())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.LoginRequestDto;
import com.ayd.sie.auth.application.dto.LoginResponseDto;
import com.ayd.sie.config.AppProperties;
import com.ayd.sie.shared.domain.entities.RefreshToken;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.AccountLockedException;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.domain.services.NotificationService;
import com.ayd.sie.shared.infrastructure.persistence.RefreshTokenJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import com.ayd.sie.shared.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class LoginUseCase {

    private final UserJpaRepository userRepository;
    private final RefreshTokenJpaRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AppProperties appProperties;
    private final NotificationService notificationService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public LoginResponseDto execute(LoginRequestDto request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmailAndActiveTrue(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        validateUserAccount(user);
        validatePassword(user, request.getPassword());

        if (user.isTwoFactorRequired()) {
            generateAndSendTwoFactorCode(user);
            return LoginResponseDto.builder()
                    .twoFactorRequired(true)
                    .userInfo(buildUserInfo(user))
                    .build();
        }

        updateUserLoginSuccess(user);

        String accessToken = jwtUtil.generateToken(
                user.getEmail(),
                user.getUserId(),
                user.getRole().getRoleName());

        RefreshToken refreshToken = createRefreshToken(user, ipAddress, userAgent);

        log.info("User logged in successfully: {}", user.getEmail());

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getTokenHash())
                .expiresIn(appProperties.getJwt().getExpiration() / 1000)
                .userInfo(buildUserInfo(user))
                .twoFactorRequired(false)
                .build();
    }

    private void validateUserAccount(User user) {
        if (!user.isAccountNonLocked()) {
            // Send notification about locked account
            notificationService.sendAccountLockedNotification(user);
            throw new AccountLockedException("Account is temporarily locked due to failed login attempts");
        }

        if (!user.isEnabled()) {
            throw new InvalidCredentialsException("Account is disabled");
        }
    }

    private void validatePassword(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= appProperties.getSecurity().getMaxLoginAttempts()) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(
                    appProperties.getSecurity().getLockoutDuration()));
            log.warn("User account locked due to failed attempts: {}", user.getEmail());

            // Send notification about account lock
            notificationService.sendAccountLockedNotification(user);
        }

        userRepository.save(user);
    }

    private void updateUserLoginSuccess(User user) {
        user.setLastLogin(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    private void generateAndSendTwoFactorCode(User user) {
        String code = String.format("%06d", secureRandom.nextInt(1000000));
        user.setTwoFactorCode(code);
        user.setTwoFactorExpiration(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        // Send 2FA code via email
        notificationService.sendTwoFactorCode(user, code);

        log.info("2FA code generated and email sent for user: {}", user.getEmail());
    }

    private RefreshToken createRefreshToken(User user, String ipAddress, String userAgent) {
        // Revoke existing tokens
        refreshTokenRepository.revokeAllUserTokens(user, LocalDateTime.now());

        // Generate new token
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
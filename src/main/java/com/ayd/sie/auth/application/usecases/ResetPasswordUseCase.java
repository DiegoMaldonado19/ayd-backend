package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.ResetPasswordRequestDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.domain.exceptions.InvalidTokenException;
import com.ayd.sie.shared.infrastructure.persistence.RefreshTokenJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import com.ayd.sie.shared.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordUseCase {

    private final UserJpaRepository userRepository;
    private final RefreshTokenJpaRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void execute(ResetPasswordRequestDto request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidCredentialsException("Passwords do not match");
        }

        String email;
        try {
            email = jwtUtil.extractUsername(request.getResetToken());
            String tokenType = jwtUtil.extractClaim(request.getResetToken(), 
                claims -> claims.get("tokenType", String.class));
            
            if (!"PASSWORD_RESET".equals(tokenType)) {
                throw new InvalidTokenException("Invalid token type");
            }

            if (jwtUtil.isTokenExpired(request.getResetToken())) {
                throw new InvalidTokenException("Reset token has expired");
            }
        } catch (Exception e) {
            log.warn("Invalid reset token used: {}", e.getMessage());
            throw new InvalidTokenException("Invalid or expired reset token");
        }

        User user = userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        String hashedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(hashedPassword);
        user.setUpdatedAt(LocalDateTime.now());
        
        refreshTokenRepository.revokeAllUserTokens(user, LocalDateTime.now());
        
        userRepository.save(user);

        log.info("Password reset successfully for user: {}", email);
    }
}
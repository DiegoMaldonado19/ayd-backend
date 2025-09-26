package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.ResetPasswordRequestDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.domain.exceptions.InvalidTokenException;
import com.ayd.sie.shared.infrastructure.persistence.RefreshTokenJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordUseCase {

    private final UserJpaRepository userRepository;
    private final RefreshTokenJpaRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(ResetPasswordRequestDto request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidCredentialsException("Passwords do not match");
        }

        Optional<User> optionalUser = userRepository.findByPasswordResetToken(request.getResetToken());

        if (optionalUser.isEmpty()) {
            throw new InvalidTokenException("Invalid reset token");
        }

        User user = optionalUser.get();

        if (!user.isPasswordResetTokenValid(request.getResetToken())) {
            throw new InvalidTokenException("Invalid or expired reset token");
        }

        String hashedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(hashedPassword);
        user.clearPasswordResetToken();
        user.setUpdatedAt(LocalDateTime.now());

        refreshTokenRepository.revokeAllUserTokens(user, LocalDateTime.now());

        userRepository.save(user);

        log.info("Password reset successfully for user: {}", user.getEmail());
    }
}
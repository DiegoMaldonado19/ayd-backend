package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.ConfirmDisable2FARequestDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmDisable2FAUseCase {

    private final UserJpaRepository userRepository;

    @Transactional
    public void execute(ConfirmDisable2FARequestDto request, String currentUserEmail) {
        User user = userRepository.findByEmailAndActiveTrue(currentUserEmail)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            throw new InvalidCredentialsException("Two-factor authentication is not enabled");
        }

        if (!user.isTwoFactorCodeValid(request.getVerificationCode())) {
            throw new InvalidCredentialsException("Invalid or expired verification code");
        }

        user.setTwoFactorEnabled(false);
        user.setTwoFactorCode(null);
        user.setTwoFactorExpiration(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("2FA disabled successfully for user: {}", currentUserEmail);
    }
}
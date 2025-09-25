package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.Disable2FARequestDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class Disable2FAUseCase {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(Disable2FARequestDto request, String currentUserEmail) {
        User user = userRepository.findByEmailAndActiveTrue(currentUserEmail)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        if (!Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            throw new InvalidCredentialsException("Two-factor authentication is not enabled");
        }

        if (!user.isTwoFactorCodeValid(request.getVerificationCode())) {
            throw new InvalidCredentialsException("Invalid verification code");
        }

        user.setTwoFactorEnabled(false);
        user.setTwoFactorCode(null);
        user.setTwoFactorExpiration(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("2FA disabled for user: {}", currentUserEmail);
    }
}
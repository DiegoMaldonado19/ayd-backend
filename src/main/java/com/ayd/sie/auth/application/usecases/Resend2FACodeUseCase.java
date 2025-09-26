package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.Resend2FACodeRequestDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.domain.services.NotificationService;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class Resend2FACodeUseCase {

    private final UserJpaRepository userRepository;
    private final NotificationService notificationService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void execute(Resend2FACodeRequestDto request) {
        User user = userRepository.findByEmailAndActiveTrue(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            throw new InvalidCredentialsException("Two-factor authentication is not enabled for this user");
        }

        String code = String.format("%06d", secureRandom.nextInt(1000000));
        user.setTwoFactorCode(code);
        user.setTwoFactorExpiration(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        notificationService.sendTwoFactorCode(user, code);

        log.info("2FA code resent to user: {}", request.getEmail());
    }
}
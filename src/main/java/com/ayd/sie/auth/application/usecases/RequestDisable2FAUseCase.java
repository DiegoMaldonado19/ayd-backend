package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.RequestDisable2FARequestDto;
import com.ayd.sie.auth.application.dto.RequestDisable2FAResponseDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.domain.services.NotificationService;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestDisable2FAUseCase {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public RequestDisable2FAResponseDto execute(RequestDisable2FARequestDto request, String currentUserEmail) {
        User user = userRepository.findByEmailAndActiveTrue(currentUserEmail)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        if (!Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            throw new InvalidCredentialsException("Two-factor authentication is not enabled");
        }

        String code = String.format("%06d", secureRandom.nextInt(1000000));
        user.setTwoFactorCode(code);
        user.setTwoFactorExpiration(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        notificationService.sendTwoFactorCode(user, code);

        log.info("2FA disable code sent to user: {}", currentUserEmail);

        return RequestDisable2FAResponseDto.builder()
                .codeSent(true)
                .message("Verification code sent to your email. Use it to confirm 2FA deactivation.")
                .build();
    }
}
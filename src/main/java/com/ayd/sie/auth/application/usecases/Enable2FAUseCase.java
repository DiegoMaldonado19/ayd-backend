package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.Enable2FARequestDto;
import com.ayd.sie.auth.application.dto.Enable2FAResponseDto;
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
public class Enable2FAUseCase {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Enable2FAResponseDto execute(Enable2FARequestDto request, String currentUserEmail) {
        User user = userRepository.findByEmailAndActiveTrue(currentUserEmail)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        if (Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            return Enable2FAResponseDto.builder()
                    .twoFactorEnabled(true)
                    .message("Two-factor authentication is already enabled")
                    .build();
        }

        user.setTwoFactorEnabled(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("2FA enabled for user: {}", currentUserEmail);

        return Enable2FAResponseDto.builder()
                .twoFactorEnabled(true)
                .message("Two-factor authentication enabled successfully")
                .build();
    }
}
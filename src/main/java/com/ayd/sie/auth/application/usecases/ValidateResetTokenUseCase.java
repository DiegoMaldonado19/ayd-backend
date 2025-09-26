package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.ValidateResetTokenRequestDto;
import com.ayd.sie.auth.application.dto.ValidateResetTokenResponseDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidateResetTokenUseCase {

    private final UserJpaRepository userRepository;

    public ValidateResetTokenResponseDto execute(ValidateResetTokenRequestDto request) {
        Optional<User> optionalUser = userRepository.findByPasswordResetToken(request.getResetToken());

        if (optionalUser.isEmpty()) {
            return ValidateResetTokenResponseDto.builder()
                    .isValid(false)
                    .build();
        }

        User user = optionalUser.get();

        if (!user.isPasswordResetTokenValid(request.getResetToken())) {
            return ValidateResetTokenResponseDto.builder()
                    .isValid(false)
                    .build();
        }

        long minutesUntilExpiration = ChronoUnit.MINUTES.between(
                LocalDateTime.now(),
                user.getPasswordResetExpiration());

        return ValidateResetTokenResponseDto.builder()
                .isValid(true)
                .expiresInMinutes(minutesUntilExpiration)
                .email(user.getEmail())
                .build();
    }
}
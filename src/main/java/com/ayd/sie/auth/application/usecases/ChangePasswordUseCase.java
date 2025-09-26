package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.ChangePasswordRequestDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.RefreshTokenJpaRepository;
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
public class ChangePasswordUseCase {

    private final UserJpaRepository userRepository;
    private final RefreshTokenJpaRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(ChangePasswordRequestDto request, String currentUserEmail) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidCredentialsException("New passwords do not match");
        }

        User user = userRepository.findByEmailAndActiveTrue(currentUserEmail)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("New password must be different from current password");
        }

        String hashedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(hashedPassword);
        user.setUpdatedAt(LocalDateTime.now());
        
        refreshTokenRepository.revokeAllUserTokens(user, LocalDateTime.now());
        
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", currentUserEmail);
    }
}
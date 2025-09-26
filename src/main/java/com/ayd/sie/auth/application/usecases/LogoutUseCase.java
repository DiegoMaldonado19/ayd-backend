package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.infrastructure.persistence.RefreshTokenJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutUseCase {

    private final RefreshTokenJpaRepository refreshTokenRepository;
    private final UserJpaRepository userRepository;

    @Transactional
    public void execute(String email) {
        User user = userRepository.findByEmailAndActiveTrue(email)
                .orElse(null);

        if (user != null) {
            int revokedTokens = refreshTokenRepository.revokeAllUserTokens(user, LocalDateTime.now());
            log.info("User logged out successfully. Revoked {} tokens for user: {}", revokedTokens, email);
        }
    }
}
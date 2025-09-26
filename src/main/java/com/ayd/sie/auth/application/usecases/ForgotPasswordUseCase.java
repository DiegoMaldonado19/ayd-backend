package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.ForgotPasswordRequestDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.services.NotificationService;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordUseCase {

    private final UserJpaRepository userRepository;
    private final NotificationService notificationService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void execute(ForgotPasswordRequestDto request) {
        Optional<User> optionalUser = userRepository.findByEmailAndActiveTrue(request.getEmail());

        if (optionalUser.isEmpty()) {
            log.warn("Password reset requested for non-existent email: {}", request.getEmail());
            return;
        }

        User user = optionalUser.get();

        if (!user.isEnabled()) {
            log.warn("Password reset requested for disabled user: {}", request.getEmail());
            return;
        }

        String resetToken = generateShortResetToken();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetExpiration(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        notificationService.sendPasswordResetEmail(user, resetToken);

        log.info("Password reset email sent to user: {}", request.getEmail());
    }

    private String generateShortResetToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder token = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            token.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }

        return token.toString();
    }
}
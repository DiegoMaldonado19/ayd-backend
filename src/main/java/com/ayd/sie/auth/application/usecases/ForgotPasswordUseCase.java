package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.ForgotPasswordRequestDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.services.NotificationService;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import com.ayd.sie.shared.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordUseCase {

    private final UserJpaRepository userRepository;
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

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

        String resetToken = generatePasswordResetToken(user);
        
        notificationService.sendPasswordResetEmail(user, resetToken);
        
        log.info("Password reset email sent to user: {}", request.getEmail());
    }

    private String generatePasswordResetToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("tokenType", "PASSWORD_RESET");
        claims.put("generatedAt", LocalDateTime.now().toString());

        return jwtUtil.generatePasswordResetToken(user.getEmail(), claims);
    }
}
package com.ayd.sie.shared.infrastructure.notifications;

import com.ayd.sie.shared.application.dto.EmailRequestDto;
import com.ayd.sie.shared.application.dto.EmailResponseDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;

    @Override
    public void sendTwoFactorCode(User user, String code) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getFullName());
            variables.put("code", code);

            EmailRequestDto emailRequest = EmailRequestDto.builder()
                    .to(user.getEmail())
                    .subject("🔐 Código de Verificación - SIE")
                    .templateName("two-factor-code")
                    .variables(variables)
                    .isHtml(true)
                    .build();

            EmailResponseDto response = emailService.sendEmail(emailRequest);

            if (response.isSent()) {
                log.info("2FA code email sent successfully to user: {}", user.getEmail());
            } else {
                log.error("Failed to send 2FA code email to user: {}. Error: {}",
                        user.getEmail(), response.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Exception sending 2FA code email to user: {}. Error: {}",
                    user.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    public void sendWelcomeEmail(User user, String temporaryPassword) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getFullName());
            variables.put("email", user.getEmail());
            variables.put("temporaryPassword", temporaryPassword);

            EmailRequestDto emailRequest = EmailRequestDto.builder()
                    .to(user.getEmail())
                    .subject("🚚 Bienvenido al Sistema Integral de Entregas (SIE)")
                    .templateName("welcome")
                    .variables(variables)
                    .isHtml(true)
                    .build();

            EmailResponseDto response = emailService.sendEmail(emailRequest);

            if (response.isSent()) {
                log.info("Welcome email sent successfully to user: {}", user.getEmail());
            } else {
                log.error("Failed to send welcome email to user: {}. Error: {}",
                        user.getEmail(), response.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Exception sending welcome email to user: {}. Error: {}",
                    user.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getFullName());
            variables.put("resetToken", resetToken);

            EmailRequestDto emailRequest = EmailRequestDto.builder()
                    .to(user.getEmail())
                    .subject("🔑 Restablecer Contraseña - SIE")
                    .templateName("password-reset")
                    .variables(variables)
                    .isHtml(true)
                    .build();

            EmailResponseDto response = emailService.sendEmail(emailRequest);

            if (response.isSent()) {
                log.info("Password reset email sent successfully to user: {}", user.getEmail());
            } else {
                log.error("Failed to send password reset email to user: {}. Error: {}",
                        user.getEmail(), response.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Exception sending password reset email to user: {}. Error: {}",
                    user.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    public void sendAccountLockedNotification(User user) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getFullName());

            EmailRequestDto emailRequest = EmailRequestDto.builder()
                    .to(user.getEmail())
                    .subject("⚠️ Cuenta Temporalmente Bloqueada - SIE")
                    .templateName("account-locked")
                    .variables(variables)
                    .isHtml(true)
                    .build();

            EmailResponseDto response = emailService.sendEmail(emailRequest);

            if (response.isSent()) {
                log.info("Account locked notification sent successfully to user: {}", user.getEmail());
            } else {
                log.error("Failed to send account locked notification to user: {}. Error: {}",
                        user.getEmail(), response.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Exception sending account locked notification to user: {}. Error: {}",
                    user.getEmail(), e.getMessage(), e);
        }
    }
}
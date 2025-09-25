package com.ayd.sie.shared.infrastructure.notifications;

import com.ayd.sie.shared.application.dto.EmailRequestDto;
import com.ayd.sie.shared.application.dto.EmailResponseDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateService templateService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public EmailResponseDto sendEmail(EmailRequestDto emailRequest) {
        try {
            String content;
            if (emailRequest.getTemplateName() != null && !emailRequest.getTemplateName().isEmpty()) {
                content = templateService.processTemplate(emailRequest.getTemplateName(), emailRequest.getVariables());
            } else {
                content = (String) emailRequest.getVariables().getOrDefault("content", "");
            }

            if (emailRequest.isHtml()) {
                return sendHtmlEmail(emailRequest.getTo(), emailRequest.getSubject(), content);
            } else {
                return sendSimpleEmail(emailRequest.getTo(), emailRequest.getSubject(), content);
            }
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", emailRequest.getTo(), e.getMessage(), e);
            return EmailResponseDto.builder()
                    .sent(false)
                    .errorMessage(e.getMessage())
                    .sentAt(LocalDateTime.now())
                    .build();
        }
    }

    @Override
    public EmailResponseDto sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);

            String messageId = UUID.randomUUID().toString();
            log.info("Simple email sent successfully to {} with messageId: {}", to, messageId);

            return EmailResponseDto.builder()
                    .sent(true)
                    .messageId(messageId)
                    .sentAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error sending simple email to {}: {}", to, e.getMessage(), e);
            return EmailResponseDto.builder()
                    .sent(false)
                    .errorMessage(e.getMessage())
                    .sentAt(LocalDateTime.now())
                    .build();
        }
    }

    @Override
    public EmailResponseDto sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            String messageId = UUID.randomUUID().toString();
            log.info("HTML email sent successfully to {} with messageId: {}", to, messageId);

            return EmailResponseDto.builder()
                    .sent(true)
                    .messageId(messageId)
                    .sentAt(LocalDateTime.now())
                    .build();

        } catch (MessagingException e) {
            log.error("Error sending HTML email to {}: {}", to, e.getMessage(), e);
            return EmailResponseDto.builder()
                    .sent(false)
                    .errorMessage(e.getMessage())
                    .sentAt(LocalDateTime.now())
                    .build();
        }
    }
}
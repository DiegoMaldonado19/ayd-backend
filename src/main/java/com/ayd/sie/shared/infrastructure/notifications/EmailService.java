package com.ayd.sie.shared.infrastructure.notifications;

import com.ayd.sie.shared.application.dto.EmailRequestDto;
import com.ayd.sie.shared.application.dto.EmailResponseDto;

public interface EmailService {

    EmailResponseDto sendEmail(EmailRequestDto emailRequest);

    EmailResponseDto sendSimpleEmail(String to, String subject, String content);

    EmailResponseDto sendHtmlEmail(String to, String subject, String htmlContent);
}
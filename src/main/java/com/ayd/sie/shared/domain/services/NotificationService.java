package com.ayd.sie.shared.domain.services;

import com.ayd.sie.shared.domain.entities.User;

public interface NotificationService {

    void sendTwoFactorCode(User user, String code);

    void sendWelcomeEmail(User user, String temporaryPassword);

    void sendPasswordResetEmail(User user, String resetToken);

    void sendAccountLockedNotification(User user);

    void sendBusinessNotification(String email, String subject, String message);

    void sendCourierNotification(String email, String subject, String message);

    void sendCancellationNotification(String email, String subject, String message, String guideNumber);

    void sendIncidentNotification(String email, String subject, String message, String guideNumber);

    void sendAssignmentNotification(String email, String subject, String message, String guideNumber);
}
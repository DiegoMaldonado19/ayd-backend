package com.ayd.sie.tracking.application.usecases;

import com.ayd.sie.shared.domain.entities.*;
import com.ayd.sie.shared.infrastructure.persistence.*;
import com.ayd.sie.shared.infrastructure.notifications.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrackingNotificationUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final NotificationJpaRepository notificationRepository;
    private final NotificationTypeJpaRepository notificationTypeRepository;
    private final EmailService emailService;

    public void sendStateChangeNotification(Integer guideId, String stateName, Integer userId) {
        log.info("Sending notification for guide {} - state: {}", guideId, stateName);

        try {
            TrackingGuide guide = trackingGuideRepository.findById(guideId)
                    .orElseThrow(() -> new RuntimeException("Guide not found: " + guideId));

            // Get notification type based on state
            NotificationType notificationType = getNotificationTypeForState(stateName);
            if (notificationType == null) {
                log.debug("No notification type found for state: {}", stateName);
                return;
            }

            // Generate notification message
            String message = generateNotificationMessage(notificationType.getTemplate(), guide);

            // Create notification record
            Notification notification = Notification.builder()
                    .guide(guide)
                    .notificationType(notificationType)
                    .user(userId != null ? User.builder().userId(userId).build() : null)
                    .message(message)
                    .sentTo(getRecipientEmail(guide))
                    .sent(false)
                    .build();

            notificationRepository.save(notification);

            // Send email notification
            sendEmailNotification(guide, message, notification.getSentTo());

            // Update notification as sent
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);

            log.info("Notification sent successfully for guide: {}", guide.getGuideNumber());

        } catch (Exception e) {
            log.error("Error sending notification for guide {}: {}", guideId, e.getMessage(), e);
        }
    }

    public void sendDeliveryStatusNotifications(TrackingGuide guide) {
        String stateName = guide.getCurrentState().getStateName();

        // Send notifications for specific states
        switch (stateName) {
            case "En Ruta":
                sendStateChangeNotification(guide.getGuideId(), "En Ruta",
                        guide.getCourier() != null ? guide.getCourier().getUserId() : null);
                break;
            case "Entrega Proxima":
                sendStateChangeNotification(guide.getGuideId(), "Entrega Proxima",
                        guide.getCourier() != null ? guide.getCourier().getUserId() : null);
                break;
            case "Entregada":
                sendStateChangeNotification(guide.getGuideId(), "Entregado",
                        guide.getCourier() != null ? guide.getCourier().getUserId() : null);
                break;
        }
    }

    private NotificationType getNotificationTypeForState(String stateName) {
        String typeName = switch (stateName) {
            case "En Ruta" -> "En Ruta";
            case "Entrega Proxima" -> "Entrega Proxima";
            case "Entregada", "Entregado" -> "Entregado";
            default -> null;
        };

        if (typeName == null) {
            return null;
        }

        return notificationTypeRepository.findByTypeNameAndActiveTrue(typeName).orElse(null);
    }

    private String generateNotificationMessage(String template, TrackingGuide guide) {
        if (template == null) {
            return String.format("Your package with guide number %s has been updated.",
                    guide.getGuideNumber());
        }

        return template.replace("{guide_number}", guide.getGuideNumber())
                .replace("{recipient_name}", guide.getRecipientName())
                .replace("{business_name}", guide.getBusiness().getBusinessName())
                .replace("{courier_name}",
                        guide.getCourier() != null
                                ? guide.getCourier().getFirstName() + " " + guide.getCourier().getLastName()
                                : "");
    }

    private String getRecipientEmail(TrackingGuide guide) {
        // For customer notifications, we would need recipient's email
        // Since it's not in the current model, we'll use a placeholder approach
        // In a real system, you might have a separate Customer entity with email
        return guide.getRecipientName().toLowerCase().replace(" ", ".") + "@customer.example.com";
    }

    private void sendEmailNotification(TrackingGuide guide, String message, String recipientEmail) {
        try {
            String subject = "Delivery Update - Guide " + guide.getGuideNumber();

            String emailBody = String.format("""
                    Dear %s,

                    %s

                    Delivery Details:
                    - Guide Number: %s
                    - Recipient: %s
                    - Address: %s, %s, %s
                    - Business: %s
                    %s

                    Thank you for using our delivery service.

                    Best regards,
                    SIE Delivery Team
                    """,
                    guide.getRecipientName(),
                    message,
                    guide.getGuideNumber(),
                    guide.getRecipientName(),
                    guide.getRecipientAddress(),
                    guide.getRecipientCity(),
                    guide.getRecipientState(),
                    guide.getBusiness().getBusinessName(),
                    guide.getCourier() != null
                            ? "- Courier: " + guide.getCourier().getFirstName() + " " + guide.getCourier().getLastName()
                            : "");

            emailService.sendSimpleEmail(recipientEmail, subject, emailBody);

        } catch (Exception e) {
            log.error("Error sending email notification for guide {}: {}", guide.getGuideNumber(), e.getMessage());
            throw new RuntimeException("Failed to send email notification", e);
        }
    }
}
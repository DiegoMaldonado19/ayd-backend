package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.CancellationDto;
import com.ayd.sie.coordinator.application.dto.ProcessCancellationRequestDto;
import com.ayd.sie.shared.domain.entities.*;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.domain.exceptions.ValidationException;
import com.ayd.sie.shared.domain.services.NotificationService;
import com.ayd.sie.shared.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessCancellationUseCase {

        private final TrackingGuideJpaRepository trackingGuideRepository;
        private final CancellationJpaRepository cancellationRepository;
        private final CancellationTypeJpaRepository cancellationTypeRepository;
        private final UserJpaRepository userRepository;
        private final NotificationService notificationService;
        private final StateHistoryJpaRepository stateHistoryRepository;
        private final TrackingStateJpaRepository trackingStateRepository;

        @Transactional
        public CancellationDto execute(ProcessCancellationRequestDto request, Integer coordinatorId) {

                TrackingGuide guide = trackingGuideRepository.findById(request.getGuideId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Guide not found with ID: " + request.getGuideId()));

                // Validate cancellation is allowed
                validateCancellationPossible(guide);

                User coordinator = userRepository.findById(coordinatorId)
                                .orElseThrow(() -> new ResourceNotFoundException("Coordinator not found"));

                CancellationType cancellationType = cancellationTypeRepository.findById(request.getCancellationTypeId())
                                .orElseThrow(() -> new ResourceNotFoundException("Cancellation type not found"));

                // Calculate penalty and commission based on business loyalty level
                PenaltyCalculation calculation = calculatePenaltyAndCommission(guide, cancellationType);

                // Create cancellation record (without processedAt and coordinatorNotes)
                Cancellation cancellation = Cancellation.builder()
                                .guide(guide)
                                .cancelledByUser(coordinator)
                                .cancellationType(cancellationType)
                                .reason(request.getReason())
                                .penaltyAmount(calculation.penaltyAmount)
                                .courierCommission(calculation.courierCommission)
                                .cancelledAt(LocalDateTime.now())
                                .build();

                cancellation = cancellationRepository.save(cancellation);

                // Add coordinator notes to guide observations if provided
                if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
                        String currentObservations = guide.getObservations();
                        String coordinatorNote = String.format("[Cancellation - %s]: %s",
                                        LocalDateTime.now(), request.getNotes());

                        if (currentObservations != null && !currentObservations.trim().isEmpty()) {
                                guide.setObservations(currentObservations + "\n" + coordinatorNote);
                        } else {
                                guide.setObservations(coordinatorNote);
                        }
                }

                // Update guide state to cancelled
                updateGuideStateToCancelled(guide);

                // Send notifications
                sendCancellationNotifications(guide, cancellation, cancellationType.getTypeName());

                log.info("Cancellation processed successfully for guide {} by coordinator {}",
                                guide.getGuideNumber(), coordinatorId);

                return mapToCancellationDto(cancellation, request.getNotes());
        }

        private void validateCancellationPossible(TrackingGuide guide) {
                String currentState = guide.getCurrentState().getStateName();

                // Cannot cancel if already picked up by courier
                if ("Recolectada".equals(currentState) || "En camino".equals(currentState) ||
                                "Entregada".equals(currentState) || "Cancelada".equals(currentState) ||
                                "Rechazada".equals(currentState)) {
                        throw new ValidationException("Cannot cancel delivery in current state: " + currentState);
                }
        }

        private PenaltyCalculation calculatePenaltyAndCommission(TrackingGuide guide,
                        CancellationType cancellationType) {
                BigDecimal courierCommission = guide.getCourierCommission() != null
                                ? guide.getCourierCommission()
                                : BigDecimal.ZERO;

                // Get business loyalty level for penalty calculation
                LoyaltyLevel loyaltyLevel = guide.getBusiness().getLoyaltyLevel();

                BigDecimal penaltyPercentage = loyaltyLevel != null
                                ? loyaltyLevel.getPenaltyPercentage()
                                : BigDecimal.valueOf(15.0); // Default 15%

                BigDecimal penaltyAmount = BigDecimal.ZERO;
                BigDecimal finalCommission = BigDecimal.ZERO;

                // If business cancellation (type_id = 1)
                if (cancellationType.getCancellationTypeId() == 1) {
                        // Check monthly free cancellations
                        Integer freeCancellations = loyaltyLevel != null ? loyaltyLevel.getFreeCancellations() : 0;
                        long monthlyCancellations = getMonthlyCancellationsCount(guide.getBusiness().getBusinessId());

                        if (monthlyCancellations >= freeCancellations) {
                                // Apply penalty on courier commission
                                finalCommission = courierCommission
                                                .multiply(penaltyPercentage.divide(BigDecimal.valueOf(100)));
                                penaltyAmount = finalCommission;
                        }
                } else {
                        // Customer rejection - courier gets full commission
                        finalCommission = courierCommission;
                }

                return new PenaltyCalculation(penaltyAmount, finalCommission);
        }

        private long getMonthlyCancellationsCount(Integer businessId) {
                LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0)
                                .withSecond(0);
                LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

                return cancellationRepository.findByCancelledAtBetween(startOfMonth, endOfMonth)
                                .stream()
                                .filter(c -> c.getGuide().getBusiness().getBusinessId().equals(businessId))
                                .filter(c -> c.getCancellationType().getCancellationTypeId() == 1)
                                .count();
        }

        private void updateGuideStateToCancelled(TrackingGuide guide) {
                String newStateName = "Cancelada";
                TrackingState cancelledState = trackingStateRepository.findByStateName(newStateName)
                                .orElseThrow(() -> new ResourceNotFoundException("State not found: " + newStateName));

                guide.setCurrentState(cancelledState);
                guide.setCancellationDate(LocalDateTime.now());
                guide.setUpdatedAt(LocalDateTime.now());

                trackingGuideRepository.save(guide);

                // Log state history
                StateHistory history = StateHistory.builder()
                                .guide(guide)
                                .state(cancelledState)
                                .user(guide.getCoordinator())
                                .observations("Guide cancelled")
                                .changedAt(LocalDateTime.now())
                                .build();

                stateHistoryRepository.save(history);
        }

        private void sendCancellationNotifications(TrackingGuide guide, Cancellation cancellation,
                        String cancellationType) {
                try {
                        // Notify business
                        if (guide.getBusiness() != null && guide.getBusiness().getEmail() != null) {
                                String businessSubject = "Cancelación de Entrega - Guía " + guide.getGuideNumber();
                                String businessMessage = String.format(
                                                "La entrega con guía %s ha sido cancelada.\n\n" +
                                                                "Tipo de cancelación: %s\n" +
                                                                "Motivo: %s\n" +
                                                                "Penalización aplicada: Q%.2f",
                                                guide.getGuideNumber(),
                                                cancellationType,
                                                cancellation.getReason(),
                                                cancellation.getPenaltyAmount());

                                notificationService.sendBusinessNotification(
                                                guide.getBusiness().getEmail(),
                                                businessSubject,
                                                businessMessage);
                        }

                        // Notify courier if assigned
                        if (guide.getAssignedCourier() != null) {
                                String courierSubject = "Cancelación de Entrega - Guía " + guide.getGuideNumber();
                                String courierMessage = String.format(
                                                "La entrega con guía %s ha sido cancelada.\n\n" +
                                                                "Comisión por entrega cancelada: Q%.2f",
                                                guide.getGuideNumber(),
                                                cancellation.getCourierCommission());

                                notificationService.sendCourierNotification(
                                                guide.getAssignedCourier().getEmail(),
                                                courierSubject,
                                                courierMessage);
                        }
                } catch (Exception e) {
                        log.error("Error sending cancellation notifications: {}", e.getMessage());
                }
        }

        private CancellationDto mapToCancellationDto(Cancellation cancellation, String coordinatorNotes) {
                TrackingGuide guide = cancellation.getGuide();

                return CancellationDto.builder()
                                .cancellationId(cancellation.getCancellationId())
                                .guideId(guide.getGuideId())
                                .guideNumber(guide.getGuideNumber())
                                .cancellationTypeId(cancellation.getCancellationType().getCancellationTypeId())
                                .cancellationTypeName(cancellation.getCancellationType().getTypeName())
                                .reason(cancellation.getReason())
                                .penaltyAmount(cancellation.getPenaltyAmount().doubleValue())
                                .courierCommission(cancellation.getCourierCommission().doubleValue())
                                .cancelledByName(cancellation.getCancelledByUser().getFirstName() + " " +
                                                cancellation.getCancelledByUser().getLastName())
                                .cancelledAt(cancellation.getCancelledAt())
                                .processedAt(cancellation.getCancelledAt()) // Use cancelledAt as processedAt
                                .businessName(guide.getBusiness().getBusinessName())
                                .coordinatorNotes(coordinatorNotes) // Pass notes from request, not from entity
                                .basePrice(guide.getBasePrice().doubleValue())
                                .currentState(guide.getCurrentState().getStateName())
                                .assignedCourierName(
                                                guide.getCourier() != null
                                                                ? guide.getCourier().getFirstName() + " "
                                                                                + guide.getCourier().getLastName()
                                                                : null)
                                .recipientName(guide.getRecipientName())
                                .recipientAddress(guide.getRecipientAddress())
                                .build();
        }

        private static class PenaltyCalculation {
                final BigDecimal penaltyAmount;
                final BigDecimal courierCommission;

                PenaltyCalculation(BigDecimal penaltyAmount, BigDecimal courierCommission) {
                        this.penaltyAmount = penaltyAmount;
                        this.courierCommission = courierCommission;
                }
        }
}
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
        private final LoyaltyLevelJpaRepository loyaltyLevelRepository;
        private final NotificationService notificationService;

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

                // Create cancellation record
                Cancellation cancellation = Cancellation.builder()
                                .guide(guide)
                                .cancelledByUser(coordinator)
                                .cancellationType(cancellationType)
                                .reason(request.getReason())
                                .penaltyAmount(calculation.penaltyAmount)
                                .courierCommission(calculation.courierCommission)
                                .cancelledAt(LocalDateTime.now())
                                .processedAt(LocalDateTime.now())
                                .coordinatorNotes(request.getNotes())
                                .build();

                cancellation = cancellationRepository.save(cancellation);

                // Update guide state to cancelled
                updateGuideStateToCancelled(guide);

                // Send notifications
                sendCancellationNotifications(guide, cancellation, cancellationType.getTypeName());

                log.info("Cancellation processed successfully for guide {} by coordinator {}",
                                guide.getGuideNumber(), coordinatorId);

                return mapToCancellationDto(cancellation);
        }

        private void validateCancellationPossible(TrackingGuide guide) {
                String currentState = guide.getCurrentState().getStateName();

                // Cannot cancel if already picked up by courier
                if ("Recogida".equals(currentState) || "En Ruta".equals(currentState) ||
                                "Entregada".equals(currentState) || "Cancelada".equals(currentState)) {
                        throw new ValidationException("Cannot cancel delivery in current state: " + currentState);
                }
        }

        private PenaltyCalculation calculatePenaltyAndCommission(TrackingGuide guide,
                        CancellationType cancellationType) {
                BigDecimal basePrice = guide.getBasePrice();

                // Get business loyalty level for penalty calculation
                LoyaltyLevel loyaltyLevel = guide.getBusiness().getLoyaltyLevel();

                BigDecimal penaltyPercentage = loyaltyLevel != null ? loyaltyLevel.getPenaltyPercentage()
                                : BigDecimal.valueOf(0.10); // Default 10%

                BigDecimal penaltyAmount = basePrice.multiply(penaltyPercentage);

                // Calculate courier commission (if courier was assigned)
                BigDecimal courierCommission = BigDecimal.ZERO;
                if (guide.getAssignedCourier() != null) {
                        // Base commission is 30% of base price, but cancelled deliveries get reduced
                        // commission
                        courierCommission = basePrice.multiply(BigDecimal.valueOf(0.15)); // 15% for cancelled
                }

                return new PenaltyCalculation(penaltyAmount, courierCommission);
        }

        private void updateGuideStateToCancelled(TrackingGuide guide) {
                // Find cancelled state (assume state ID 7 for cancelled based on common
                // patterns)
                // You might need to adjust this based on your actual state configuration
                guide.getCurrentState().setStateName("Cancelada");
                trackingGuideRepository.save(guide);
        }

        private void sendCancellationNotifications(TrackingGuide guide, Cancellation cancellation,
                        String cancellationType) {
                // Notify business
                String businessSubject = "Cancelación de Entrega - Guía " + guide.getGuideNumber();
                String businessMessage = String.format(
                                "Su entrega con guía %s ha sido cancelada.\n\n" +
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
        }

        private CancellationDto mapToCancellationDto(Cancellation cancellation) {
                return CancellationDto.builder()
                                .cancellationId(cancellation.getCancellationId())
                                .guideId(cancellation.getGuide().getGuideId())
                                .guideNumber(cancellation.getGuide().getGuideNumber())
                                .cancellationTypeId(cancellation.getCancellationType().getCancellationTypeId())
                                .cancellationTypeName(cancellation.getCancellationType().getTypeName())
                                .reason(cancellation.getReason())
                                .penaltyAmount(cancellation.getPenaltyAmount().doubleValue())
                                .courierCommission(cancellation.getCourierCommission().doubleValue())
                                .cancelledByName(cancellation.getCancelledByUser().getFullName())
                                .cancelledAt(cancellation.getCancelledAt())
                                .processedAt(cancellation.getProcessedAt())
                                .businessName(cancellation.getGuide().getBusiness().getBusinessName())
                                .coordinatorNotes(cancellation.getCoordinatorNotes())
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
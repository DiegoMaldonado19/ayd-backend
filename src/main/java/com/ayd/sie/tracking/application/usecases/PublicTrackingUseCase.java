package com.ayd.sie.tracking.application.usecases;

import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.domain.entities.StateHistory;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.StateHistoryJpaRepository;
import com.ayd.sie.tracking.application.dto.TrackingResponseDto;
import com.ayd.sie.tracking.application.dto.TrackingHistoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicTrackingUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final StateHistoryJpaRepository stateHistoryRepository;

    public TrackingResponseDto getTrackingInfo(String guideNumber) {
        log.info("Getting tracking info for guide number: {}", guideNumber);

        TrackingGuide guide = trackingGuideRepository.findByGuideNumber(guideNumber)
                .orElseThrow(() -> new RuntimeException("Tracking guide not found: " + guideNumber));

        // Get status history
        List<StateHistory> history = stateHistoryRepository.findByGuideGuideIdOrderByChangedAtDesc(guide.getGuideId());

        List<TrackingHistoryDto> statusHistory = history.stream()
                .map(this::mapToHistoryDto)
                .collect(Collectors.toList());

        // Determine if delivery can be rejected
        boolean canReject = canRejectDelivery(guide);

        return TrackingResponseDto.builder()
                .guideNumber(guide.getGuideNumber())
                .currentStatus(guide.getCurrentState().getStateName())
                .recipientName(guide.getRecipientName())
                .recipientAddress(guide.getRecipientAddress())
                .recipientCity(guide.getRecipientCity())
                .recipientState(guide.getRecipientState())
                .basePrice(guide.getBasePrice())
                .createdAt(guide.getCreatedAt())
                .assignmentDate(guide.getAssignmentDate())
                .pickupDate(guide.getPickupDate())
                .deliveryDate(guide.getDeliveryDate())
                .observations(guide.getObservations())
                .canReject(canReject)
                .businessName(guide.getBusiness().getBusinessName())
                .courierName(guide.getCourier() != null
                        ? guide.getCourier().getFirstName() + " " + guide.getCourier().getLastName()
                        : null)
                .courierPhone(guide.getCourier() != null ? guide.getCourier().getPhone() : null)
                .statusHistory(statusHistory)
                .build();
    }

    private TrackingHistoryDto mapToHistoryDto(StateHistory history) {
        return TrackingHistoryDto.builder()
                .statusName(history.getState().getStateName())
                .changedAt(history.getChangedAt())
                .changedBy(history.getUser() != null
                        ? history.getUser().getFirstName() + " " + history.getUser().getLastName()
                        : "Sistema")
                .observations(history.getObservations())
                .build();
    }

    private boolean canRejectDelivery(TrackingGuide guide) {
        // Can reject if:
        // 1. Guide is in "En Ruta" or "Entrega Proxima" status
        // 2. Guide is not already in a final state
        String currentStatus = guide.getCurrentState().getStateName();
        boolean isFinal = guide.getCurrentState().getIsFinal();

        return !isFinal &&
                (currentStatus.equals("En Ruta") ||
                        currentStatus.equals("Entrega Proxima") ||
                        currentStatus.equals("Asignada") ||
                        currentStatus.equals("Recogida"));
    }
}
package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidateCancellationUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;

    @Transactional(readOnly = true)
    public boolean execute(Integer guideId) {
        // 1. Get tracking guide
        TrackingGuide guide = trackingGuideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Tracking guide not found"));

        // 2. Check current state - cancellation is only allowed before pickup
        String currentState = guide.getCurrentState().getStateName();

        // States that allow cancellation: Creada, Asignada
        boolean canCancel = currentState.equals("Creada") || currentState.equals("Asignada");

        // States that DO NOT allow cancellation: Recogida, En Ruta, Entregada,
        // Rechazada, Cancelada
        boolean cannotCancel = currentState.equals("Recogida") ||
                currentState.equals("En Ruta") ||
                currentState.equals("Entregada") ||
                currentState.equals("Rechazada") ||
                currentState.equals("Cancelada");

        log.debug("Cancellation validation for guide {}: state={}, canCancel={}",
                guide.getGuideNumber(), currentState, canCancel && !cannotCancel);

        return canCancel && !cannotCancel;
    }
}
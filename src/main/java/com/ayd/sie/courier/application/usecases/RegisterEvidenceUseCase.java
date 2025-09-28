package com.ayd.sie.courier.application.usecases;

import com.ayd.sie.courier.application.dto.EvidenceDto;
import com.ayd.sie.shared.domain.entities.DeliveryEvidence;
import com.ayd.sie.shared.domain.entities.EvidenceType;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.infrastructure.persistence.DeliveryEvidenceJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.EvidenceTypeJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import com.ayd.sie.shared.domain.exceptions.BusinessConstraintViolationException;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterEvidenceUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final UserJpaRepository userRepository;
    private final DeliveryEvidenceJpaRepository deliveryEvidenceRepository;
    private final EvidenceTypeJpaRepository evidenceTypeRepository;

    @Transactional
    public EvidenceDto execute(EvidenceDto request, Integer courierId) {
        log.info("Courier {} registering evidence for guide {}", courierId, request.getGuideId());

        // 1. Validate courier
        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        if (!courier.getRole().getRoleName().equals("Repartidor")) {
            throw new BusinessConstraintViolationException("Only couriers can register evidence");
        }

        // 2. Validate and get tracking guide
        TrackingGuide guide = trackingGuideRepository.findById(request.getGuideId())
                .orElseThrow(() -> new ResourceNotFoundException("Tracking guide not found"));

        // 3. Verify the guide is assigned to the requesting courier
        if (guide.getCourier() == null || !guide.getCourier().getUserId().equals(courierId)) {
            throw new BusinessConstraintViolationException("Guide is not assigned to this courier");
        }

        // 4. Validate guide state allows evidence registration
        String currentStateName = guide.getCurrentState().getStateName();
        if (!currentStateName.equals("En Ruta") && !currentStateName.equals("Entregada") &&
                !currentStateName.equals("Incidencia")) {
            throw new BusinessConstraintViolationException(
                    "Evidence can only be registered for deliveries in 'En Ruta', 'Entregada', or 'Incidencia' state. Current state: "
                            + currentStateName);
        }

        // 5. Validate evidence type
        EvidenceType evidenceType = evidenceTypeRepository.findByIdAndActiveTrue(request.getEvidenceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Evidence type not found or inactive"));

        // 6. Validate evidence requirements
        validateEvidenceRequirements(request, evidenceType);

        // 7. Create or update evidence
        DeliveryEvidence evidence;
        if (request.getEvidenceId() != null) {
            // Update existing evidence
            evidence = deliveryEvidenceRepository.findById(request.getEvidenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Evidence not found"));

            // Verify ownership
            if (!evidence.getGuide().getGuideId().equals(request.getGuideId())) {
                throw new BusinessConstraintViolationException("Evidence does not belong to the specified guide");
            }

            updateEvidence(evidence, request);
        } else {
            // Create new evidence
            evidence = createEvidence(guide, request);
        }

        DeliveryEvidence savedEvidence = deliveryEvidenceRepository.save(evidence);

        log.info("Evidence {} registered successfully for guide {} by courier {}",
                savedEvidence.getEvidenceId(), guide.getGuideId(), courierId);

        return mapToEvidenceDto(savedEvidence, evidenceType);
    }

    @Transactional(readOnly = true)
    public List<EvidenceDto> getGuideEvidence(Integer guideId, Integer courierId) {
        log.info("Courier {} retrieving evidence for guide {}", courierId, guideId);

        // 1. Validate courier exists
        userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        // 2. Validate and get tracking guide
        TrackingGuide guide = trackingGuideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Tracking guide not found"));

        // 3. Verify the guide is assigned to the requesting courier
        if (guide.getCourier() == null || !guide.getCourier().getUserId().equals(courierId)) {
            throw new BusinessConstraintViolationException("Guide is not assigned to this courier");
        }

        // 4. Get all evidence for the guide
        List<DeliveryEvidence> evidenceList = deliveryEvidenceRepository.findByGuideIdOrderByCreatedAtAsc(guideId);

        return evidenceList.stream()
                .map(evidence -> {
                    EvidenceType type = evidenceTypeRepository.findById(evidence.getEvidenceTypeId())
                            .orElse(null);
                    return mapToEvidenceDto(evidence, type);
                })
                .collect(Collectors.toList());
    }

    private void validateEvidenceRequirements(EvidenceDto request, EvidenceType evidenceType) {
        // For photo evidence, file URL is required
        if (evidenceType.getTypeName().equals("Fotografia") &&
                (request.getFileUrl() == null || request.getFileUrl().trim().isEmpty())) {
            throw new BusinessConstraintViolationException("File URL is required for photo evidence");
        }

        // For signature evidence, file URL is required
        if (evidenceType.getTypeName().equals("Firma") &&
                (request.getFileUrl() == null || request.getFileUrl().trim().isEmpty())) {
            throw new BusinessConstraintViolationException("File URL is required for signature evidence");
        }

        // For note evidence, notes are required
        if (evidenceType.getTypeName().equals("Nota") &&
                (request.getNotes() == null || request.getNotes().trim().isEmpty())) {
            throw new BusinessConstraintViolationException("Notes are required for note evidence");
        }
    }

    private DeliveryEvidence createEvidence(TrackingGuide guide, EvidenceDto request) {
        return DeliveryEvidence.builder()
                .guide(guide)
                .evidenceTypeId(request.getEvidenceTypeId())
                .fileUrl(request.getFileUrl())
                .notes(request.getNotes())
                .build();
    }

    private void updateEvidence(DeliveryEvidence evidence, EvidenceDto request) {
        if (request.getFileUrl() != null) {
            evidence.setFileUrl(request.getFileUrl());
        }
        if (request.getNotes() != null) {
            evidence.setNotes(request.getNotes());
        }
        // Evidence type ID cannot be changed after creation
    }

    private EvidenceDto mapToEvidenceDto(DeliveryEvidence evidence, EvidenceType evidenceType) {
        return EvidenceDto.builder()
                .evidenceId(evidence.getEvidenceId())
                .guideId(evidence.getGuide().getGuideId())
                .evidenceTypeId(evidence.getEvidenceTypeId())
                .evidenceTypeName(evidenceType != null ? evidenceType.getTypeName() : null)
                .fileUrl(evidence.getFileUrl())
                .notes(evidence.getNotes())
                .createdAt(evidence.getCreatedAt())
                .updatedAt(evidence.getUpdatedAt())
                .build();
    }
}
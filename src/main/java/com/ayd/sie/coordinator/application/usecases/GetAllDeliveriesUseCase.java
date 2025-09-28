package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.AssignmentDto;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetAllDeliveriesUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;

    public Page<AssignmentDto> execute(Pageable pageable) {
        log.info("Getting all deliveries with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        // Get all tracking guides with pagination
        Page<TrackingGuide> guides = trackingGuideRepository.findAll(pageable);

        // Map to DTOs
        return guides.map(this::mapToAssignmentDto);
    }

    private AssignmentDto mapToAssignmentDto(TrackingGuide guide) {
        return AssignmentDto.builder()
                .guideId(guide.getGuideId())
                .guideNumber(guide.getGuideNumber())
                .courierId(guide.getCourier() != null ? guide.getCourier().getUserId() : null)
                .courierName(guide.getCourier() != null
                        ? guide.getCourier().getFirstName() + " " + guide.getCourier().getLastName()
                        : null)
                .coordinatorId(guide.getCoordinator() != null ? guide.getCoordinator().getUserId() : null)
                .coordinatorName(guide.getCoordinator() != null
                        ? guide.getCoordinator().getFirstName() + " " + guide.getCoordinator().getLastName()
                        : null)
                .basePrice(guide.getBasePrice())
                .courierCommission(guide.getCourierCommission())
                .assignedAt(guide.getAssignmentDate())
                .assignmentAccepted(guide.getAssignmentAccepted())
                .assignmentAcceptedAt(guide.getAssignmentAcceptedAt())
                .businessName(guide.getBusiness().getBusinessName())
                .recipientName(guide.getRecipientName())
                .recipientAddress(guide.getRecipientAddress())
                .currentState(guide.getCurrentState().getStateName())
                .observations(guide.getObservations())
                .build();
    }
}
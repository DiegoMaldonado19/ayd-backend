package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.AssignmentDto;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetDeliveryHistoryUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;

    @Transactional(readOnly = true)
    public Page<AssignmentDto> execute(String status, String search, LocalDate startDate,
            LocalDate endDate, Pageable pageable) {

        Specification<TrackingGuide> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by status if specified
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("currentState").get("stateName"), status));
            }

            // Filter by date range
            if (startDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDateTime));
            }

            if (endDate != null) {
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDateTime));
            }

            // Add search functionality
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";

                Predicate guidePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("guideNumber")), searchPattern);

                Predicate businessPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("business").get("businessName")), searchPattern);

                Predicate recipientPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("recipientName")), searchPattern);

                Predicate addressPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("recipientAddress")), searchPattern);

                // Search in courier name if assigned
                Predicate courierPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(
                                criteriaBuilder.concat(
                                        criteriaBuilder.concat(root.get("courier").get("firstName"), " "),
                                        root.get("courier").get("lastName"))),
                        searchPattern);

                predicates.add(criteriaBuilder.or(
                        guidePredicate, businessPredicate, recipientPredicate,
                        addressPredicate, courierPredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<TrackingGuide> guides = trackingGuideRepository.findAll(spec, pageable);

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
                .assignedAt(guide.getCreatedAt()) // Placeholder - would need actual assignment timestamp
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
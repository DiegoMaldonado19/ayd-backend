package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.AssignmentDto;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetDeliveryHistoryUseCase {

        private final TrackingGuideJpaRepository trackingGuideRepository;

        public Page<AssignmentDto> execute(
                        String status,
                        String search,
                        LocalDate startDate,
                        LocalDate endDate,
                        Pageable pageable) {

                // Build specification
                Specification<TrackingGuide> spec = buildSpecification(status, search, startDate, endDate);

                // Execute query with pageable (includes sorting from controller)
                Page<TrackingGuide> guides = trackingGuideRepository.findAll(spec, pageable);

                return guides.map(this::mapToAssignmentDto);
        }

        private Specification<TrackingGuide> buildSpecification(
                        String status,
                        String search,
                        LocalDate startDate,
                        LocalDate endDate) {

                return (root, query, criteriaBuilder) -> {
                        List<Predicate> predicates = new ArrayList<>();

                        // Date range filter on assignmentDate
                        if (startDate != null && endDate != null) {
                                LocalDateTime startDateTime = startDate.atStartOfDay();
                                LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

                                predicates.add(criteriaBuilder.between(
                                                root.get("assignmentDate"),
                                                startDateTime,
                                                endDateTime));
                        }

                        // Status filter
                        if (status != null && !status.trim().isEmpty()) {
                                predicates.add(criteriaBuilder.equal(
                                                root.get("currentState").get("stateName"), status));
                        }

                        // Search filter
                        if (search != null && !search.trim().isEmpty()) {
                                String searchPattern = "%" + search.toLowerCase() + "%";

                                Predicate guidePredicate = criteriaBuilder.like(
                                                criteriaBuilder.lower(root.get("guideNumber")), searchPattern);

                                Predicate businessPredicate = criteriaBuilder.like(
                                                criteriaBuilder.lower(root.get("business").get("businessName")),
                                                searchPattern);

                                Predicate recipientPredicate = criteriaBuilder.like(
                                                criteriaBuilder.lower(root.get("recipientName")), searchPattern);

                                Predicate addressPredicate = criteriaBuilder.like(
                                                criteriaBuilder.lower(root.get("recipientAddress")), searchPattern);

                                predicates.add(criteriaBuilder.or(
                                                guidePredicate, businessPredicate, recipientPredicate,
                                                addressPredicate));
                        }

                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                };
        }

        private AssignmentDto mapToAssignmentDto(TrackingGuide guide) {
                return AssignmentDto.builder()
                                .guideId(guide.getGuideId())
                                .guideNumber(guide.getGuideNumber())
                                .courierId(guide.getCourier() != null ? guide.getCourier().getUserId() : null)
                                .courierName(guide.getCourier() != null
                                                ? guide.getCourier().getFirstName() + " "
                                                                + guide.getCourier().getLastName()
                                                : null)
                                .coordinatorId(guide.getCoordinator() != null ? guide.getCoordinator().getUserId()
                                                : null)
                                .coordinatorName(guide.getCoordinator() != null
                                                ? guide.getCoordinator().getFirstName() + " "
                                                                + guide.getCoordinator().getLastName()
                                                : null)
                                .basePrice(guide.getBasePrice())
                                .courierCommission(guide.getCourierCommission())
                                .assignedAt(guide.getAssignmentDate()) // Correct field name
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
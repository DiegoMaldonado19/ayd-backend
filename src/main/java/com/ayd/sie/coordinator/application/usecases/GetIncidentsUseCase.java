package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.IncidentDto;
import com.ayd.sie.shared.domain.entities.DeliveryIncident;
import com.ayd.sie.shared.infrastructure.persistence.DeliveryIncidentJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetIncidentsUseCase {

    private final DeliveryIncidentJpaRepository deliveryIncidentRepository;

    @Transactional(readOnly = true)
    public Page<IncidentDto> execute(Boolean resolved, String search, Pageable pageable) {

        Specification<DeliveryIncident> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by resolution status if specified
            if (resolved != null) {
                predicates.add(criteriaBuilder.equal(root.get("resolved"), resolved));
            }

            // Add search functionality
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";

                Predicate guidePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("guide").get("guideNumber")), searchPattern);

                Predicate businessPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("guide").get("business").get("businessName")), searchPattern);

                Predicate recipientPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("guide").get("recipientName")), searchPattern);

                Predicate descriptionPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), searchPattern);

                Predicate reporterPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(
                                criteriaBuilder.concat(
                                        criteriaBuilder.concat(root.get("reportedByUser").get("firstName"), " "),
                                        root.get("reportedByUser").get("lastName"))),
                        searchPattern);

                predicates.add(criteriaBuilder.or(
                        guidePredicate, businessPredicate, recipientPredicate,
                        descriptionPredicate, reporterPredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<DeliveryIncident> incidents = deliveryIncidentRepository.findAll(spec, pageable);

        return incidents.map(this::mapToIncidentDto);
    }

    private IncidentDto mapToIncidentDto(DeliveryIncident incident) {
        return IncidentDto.builder()
                .incidentId(incident.getIncidentId())
                .guideId(incident.getGuide().getGuideId())
                .guideNumber(incident.getGuide().getGuideNumber())
                .incidentTypeId(incident.getIncidentTypeId())
                .incidentTypeName("Incident Type " + incident.getIncidentTypeId()) // TODO: Get actual type name
                .reportedByUserId(incident.getReportedByUser().getUserId())
                .reportedByName(incident.getReportedByUser().getFirstName() + " " +
                        incident.getReportedByUser().getLastName())
                .reportedByRole(incident.getReportedByUser().getRole().getRoleName())
                .description(incident.getDescription())
                .resolution(incident.getResolution())
                .resolved(incident.getResolved())
                .resolvedAt(incident.getResolvedAt())
                .resolvedByUserId(
                        incident.getResolvedByUser() != null ? incident.getResolvedByUser().getUserId() : null)
                .resolvedByName(
                        incident.getResolvedByUser() != null ? incident.getResolvedByUser().getFirstName() + " " +
                                incident.getResolvedByUser().getLastName() : null)
                .businessName(
                        incident.getGuide().getBusiness() != null ? incident.getGuide().getBusiness().getBusinessName()
                                : null)
                .recipientName(incident.getGuide().getRecipientName())
                .recipientAddress(incident.getGuide().getRecipientAddress())
                .currentState(incident.getGuide().getCurrentState().getStateName())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build();
    }
}
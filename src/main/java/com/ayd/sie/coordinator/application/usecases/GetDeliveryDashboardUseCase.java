package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.DeliveryDashboardDto;
import com.ayd.sie.shared.domain.entities.DeliveryIncident;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetDeliveryDashboardUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final UserJpaRepository userRepository;
    private final ContractJpaRepository contractRepository;
    private final DeliveryIncidentJpaRepository deliveryIncidentRepository;
    private final IncidentTypeJpaRepository incidentTypeRepository;

    @Transactional(readOnly = true)
    public DeliveryDashboardDto execute(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // Get delivery counts by state for the specified date
        Long totalCreated = countDeliveriesByStateAndDate("Creada", startOfDay, endOfDay);
        Long totalAssigned = countDeliveriesByStateAndDate("Asignada", startOfDay, endOfDay);
        Long totalPickedUp = countDeliveriesByStateAndDate("Recogida", startOfDay, endOfDay);
        Long totalInRoute = countDeliveriesByStateAndDate("En Ruta", startOfDay, endOfDay);
        Long totalCompleted = countDeliveriesByStateAndDate("Entregada", startOfDay, endOfDay);
        Long totalCancelled = countDeliveriesByStateAndDate("Cancelada", startOfDay, endOfDay);
        Long totalRejected = countDeliveriesByStateAndDate("Rechazada", startOfDay, endOfDay);
        Long totalIncidents = countDeliveriesByStateAndDate("Incidencia", startOfDay, endOfDay);

        // Calculate pending assignments (created but not assigned)
        Long pendingAssignments = totalCreated;

        // Get courier statistics
        Long activeCouriers = countActiveCouriers();
        Long couriersWithContracts = countCouriersWithActiveContracts();

        // Get unresolved incidents
        Long unresolvedIncidents = countUnresolvedIncidents();

        // Calculate metrics
        Long totalDeliveries = totalCreated + totalAssigned + totalPickedUp +
                totalInRoute + totalCompleted + totalCancelled +
                totalRejected + totalIncidents;

        Double completionPercentage = totalDeliveries > 0 ? (totalCompleted * 100.0) / totalDeliveries : 0.0;

        Double efficiencyMetric = totalDeliveries > 0
                ? ((totalCompleted + totalInRoute + totalPickedUp) * 100.0) / totalDeliveries
                : 0.0;

        // Get recent pending deliveries
        List<DeliveryDashboardDto.DeliveryStatusDto> recentPendingDeliveries = getRecentPendingDeliveries();

        // Get recent incidents
        List<DeliveryDashboardDto.IncidentSummaryDto> recentIncidents = getRecentIncidents();

        // Get courier workload
        List<DeliveryDashboardDto.CourierWorkloadDto> courierWorkload = getCourierWorkload();

        return DeliveryDashboardDto.builder()
                .dashboardDate(date)
                .lastUpdated(LocalDateTime.now())
                .totalCreated(totalCreated)
                .totalAssigned(totalAssigned)
                .totalPickedUp(totalPickedUp)
                .totalInRoute(totalInRoute)
                .totalCompleted(totalCompleted)
                .totalCancelled(totalCancelled)
                .totalRejected(totalRejected)
                .totalIncidents(totalIncidents)
                .pendingAssignments(pendingAssignments)
                .activeCouriers(activeCouriers)
                .couriersWithContracts(couriersWithContracts)
                .unresolvedIncidents(unresolvedIncidents)
                .completionPercentage(Math.round(completionPercentage * 100.0) / 100.0)
                .efficiencyMetric(Math.round(efficiencyMetric * 100.0) / 100.0)
                .recentPendingDeliveries(recentPendingDeliveries)
                .recentIncidents(recentIncidents)
                .courierWorkload(courierWorkload)
                .build();
    }

    private Long countDeliveriesByStateAndDate(String stateName, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        Specification<TrackingGuide> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("currentState").get("stateName"), stateName));
            predicates.add(criteriaBuilder.between(root.get("createdAt"), startOfDay, endOfDay));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return trackingGuideRepository.count(spec);
    }

    private Long countActiveCouriers() {
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("role").get("roleName"), "Repartidor"));
            predicates.add(criteriaBuilder.equal(root.get("active"), true));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.count(spec);
    }

    private Long countCouriersWithActiveContracts() {
        return contractRepository.countCouriersWithActiveContracts();
    }

    private Long countUnresolvedIncidents() {
        return deliveryIncidentRepository.count(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("resolved"), false));
    }

    private List<DeliveryDashboardDto.DeliveryStatusDto> getRecentPendingDeliveries() {
        Specification<TrackingGuide> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("currentState").get("stateName"), "Creada"));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        List<TrackingGuide> guides = trackingGuideRepository.findAll(spec, pageRequest).getContent();

        return guides.stream().map(guide -> DeliveryDashboardDto.DeliveryStatusDto.builder()
                .guideId(guide.getGuideId())
                .guideNumber(guide.getGuideNumber())
                .businessName(guide.getBusiness().getBusinessName())
                .recipientName(guide.getRecipientName())
                .currentState(guide.getCurrentState().getStateName())
                .createdAt(guide.getCreatedAt())
                .assignedCourier(null)
                .priority("NORMAL")
                .build()).collect(Collectors.toList());
    }

    private List<DeliveryDashboardDto.IncidentSummaryDto> getRecentIncidents() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        List<DeliveryIncident> incidents = deliveryIncidentRepository.findAll(pageRequest).getContent();

        return incidents.stream().map(incident -> {
            // Get the actual incident type name from the repository
            String incidentTypeName = incidentTypeRepository.findById(incident.getIncidentTypeId())
                    .map(incidentType -> incidentType.getTypeName())
                    .orElse("Tipo de Incidente Desconocido");

            return DeliveryDashboardDto.IncidentSummaryDto.builder()
                    .incidentId(incident.getIncidentId())
                    .guideNumber(incident.getGuide().getGuideNumber())
                    .incidentType(incidentTypeName) // Get actual incident type name
                    .reportedBy(incident.getReportedByUser().getFirstName() + " " +
                            incident.getReportedByUser().getLastName())
                    .createdAt(incident.getCreatedAt())
                    .resolved(incident.getResolved())
                    .build();
        }).collect(Collectors.toList());
    }

    private List<DeliveryDashboardDto.CourierWorkloadDto> getCourierWorkload() {
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("role").get("roleName"), "Repartidor"));
            predicates.add(criteriaBuilder.equal(root.get("active"), true));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<User> couriers = userRepository.findAll(spec);

        return couriers.stream().map(courier -> {
            boolean hasActiveContract = contractRepository.findActiveContractByUserId(courier.getUserId()).isPresent();

            long assignedCount = trackingGuideRepository.countAssignedToCourier(courier.getUserId());
            long completedCount = trackingGuideRepository.countCompletedByCourier(courier.getUserId());
            long pendingCount = trackingGuideRepository.countPendingByCourier(courier.getUserId());
            long incidentsCount = trackingGuideRepository.countIncidentsByCourier(courier.getUserId());

            Double completionRate = assignedCount > 0 ? (completedCount * 100.0) / assignedCount : 0.0;

            return DeliveryDashboardDto.CourierWorkloadDto.builder()
                    .courierId(courier.getUserId())
                    .courierName(courier.getFirstName() + " " + courier.getLastName())
                    .assignedCount(assignedCount)
                    .completedCount(completedCount)
                    .pendingCount(pendingCount)
                    .incidentsCount(incidentsCount)
                    .hasActiveContract(hasActiveContract)
                    .completionRate(Math.round(completionRate * 100.0) / 100.0)
                    .build();
        }).collect(Collectors.toList());
    }
}
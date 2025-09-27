package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.AssignmentDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.infrastructure.persistence.ContractJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAvailableCouriersUseCase {

    private final UserJpaRepository userRepository;
    private final ContractJpaRepository contractRepository;
    private final TrackingGuideJpaRepository trackingGuideRepository;

    @Transactional(readOnly = true)
    public List<AssignmentDto.CourierWorkloadDto> execute() {

        // Get all active couriers with role "Repartidor"
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("role").get("roleName"), "Repartidor"));
            predicates.add(criteriaBuilder.equal(root.get("active"), true));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<User> couriers = userRepository.findAll(spec);

        return couriers.stream()
                .map(this::mapToCourierWorkloadDto)
                .collect(Collectors.toList());
    }

    private AssignmentDto.CourierWorkloadDto mapToCourierWorkloadDto(User courier) {
        // Check if courier has active contract
        boolean hasActiveContract = contractRepository.findActiveContractByUserId(courier.getUserId()).isPresent();

        // Get workload statistics
        long assignedCount = trackingGuideRepository.countAssignedToCourier(courier.getUserId());
        long completedCount = trackingGuideRepository.countCompletedByCourier(courier.getUserId());
        long pendingCount = trackingGuideRepository.countPendingByCourier(courier.getUserId());
        long incidentsCount = trackingGuideRepository.countIncidentsByCourier(courier.getUserId());

        // Calculate completion rate
        Double completionRate = 0.0;
        if (assignedCount > 0) {
            completionRate = (completedCount * 100.0) / assignedCount;
        }

        return AssignmentDto.CourierWorkloadDto.builder()
                .courierId(courier.getUserId())
                .courierName(courier.getFirstName() + " " + courier.getLastName())
                .assignedCount(assignedCount)
                .completedCount(completedCount)
                .pendingCount(pendingCount)
                .incidentsCount(incidentsCount)
                .hasActiveContract(hasActiveContract)
                .completionRate(completionRate)
                .build();
    }
}
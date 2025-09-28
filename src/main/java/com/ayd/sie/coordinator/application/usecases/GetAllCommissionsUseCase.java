package com.ayd.sie.coordinator.application.usecases;

import com.ayd.sie.coordinator.application.dto.AllCommissionsDto;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetAllCommissionsUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;

    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.15"); // 15% commission

    public Page<AllCommissionsDto> execute(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.info("Getting all commissions from {} to {} with pagination: page={}, size={}",
                startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());

        // Build specification to filter only delivered guides
        Specification<TrackingGuide> spec = buildSpecification(startDate, endDate);

        // Get delivered guides with pagination
        Page<TrackingGuide> guides = trackingGuideRepository.findAll(spec, pageable);

        // Map to DTOs
        return guides.map(this::mapToAllCommissionsDto);
    }

    private Specification<TrackingGuide> buildSpecification(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Only delivered guides have commissions
            predicates.add(criteriaBuilder.equal(root.get("currentState").get("stateName"), "Entregada"));

            // Filter by date range if provided
            if (startDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("deliveryDate"), startDateTime));
            }

            if (endDate != null) {
                LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("deliveryDate"), endDateTime));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private AllCommissionsDto mapToAllCommissionsDto(TrackingGuide guide) {
        BigDecimal commissionAmount = calculateCommission(guide);

        return AllCommissionsDto.builder()
                .guideId(guide.getGuideId())
                .guideNumber(guide.getGuideNumber())
                .courierId(guide.getCourier() != null ? guide.getCourier().getUserId() : null)
                .courierName(guide.getCourier() != null
                        ? guide.getCourier().getFirstName() + " " + guide.getCourier().getLastName()
                        : null)
                .businessName(guide.getBusiness().getBusinessName())
                .recipientName(guide.getRecipientName())
                .deliveryDate(guide.getDeliveryDate())
                .basePrice(guide.getBasePrice())
                .commissionRate(COMMISSION_RATE)
                .commissionAmount(commissionAmount)
                .currentStatus(guide.getCurrentState().getStateName())
                .settlementStatus("Pendiente") // Default status - this could come from another entity in the future
                .build();
    }

    private BigDecimal calculateCommission(TrackingGuide guide) {
        if (guide.getBasePrice() == null) {
            return BigDecimal.ZERO;
        }
        return guide.getBasePrice().multiply(COMMISSION_RATE);
    }
}
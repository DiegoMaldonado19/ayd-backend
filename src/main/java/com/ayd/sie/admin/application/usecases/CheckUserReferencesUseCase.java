package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.UserReferencesDto;
import com.ayd.sie.shared.domain.entities.Business;
import com.ayd.sie.shared.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckUserReferencesUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final StateHistoryJpaRepository stateHistoryRepository;
    private final DeliveryIncidentJpaRepository deliveryIncidentRepository;
    private final CancellationJpaRepository cancellationRepository;
    private final CourierSettlementJpaRepository courierSettlementRepository;
    private final BusinessJpaRepository businessRepository;
    private final AuditLogJpaRepository auditLogRepository;

    @Transactional(readOnly = true)
    public UserReferencesDto execute(Integer userId) {
        log.info("Checking references for user ID: {}", userId);

        // Check tracking guides as courier
        long trackingGuidesAsCourier = trackingGuideRepository.countByCourierId(userId);
        long activeGuidesAsCourier = trackingGuideRepository.countActiveByCourierId(userId);

        // Check tracking guides as coordinator
        long trackingGuidesAsCoordinator = trackingGuideRepository.countByCoordinatorId(userId);
        long activeGuidesAsCoordinator = trackingGuideRepository.countActiveByCoordinatorId(userId);

        // Check state history
        long stateHistoryEntries = stateHistoryRepository.countByUserId(userId);

        // Check delivery incidents
        long reportedIncidents = deliveryIncidentRepository.countByReportedByUserId(userId);
        long resolvedIncidents = deliveryIncidentRepository.countByResolvedByUserId(userId);

        // Check cancellations
        long cancellations = cancellationRepository.countByCancelledByUserId(userId);

        // Check courier settlements
        long courierSettlements = courierSettlementRepository.countByCourierId(userId);
        long pendingSettlements = courierSettlementRepository.countPendingByCourierId(userId);

        // Check business
        Optional<Business> business = businessRepository.findByUserUserIdAndActiveTrue(userId);
        boolean hasBusiness = business.isPresent();
        String businessName = business.map(Business::getBusinessName).orElse(null);

        // Check audit log entries
        long auditLogEntries = auditLogRepository.countByUserId(userId);

        boolean hasReferences = (trackingGuidesAsCourier > 0 ||
                trackingGuidesAsCoordinator > 0 ||
                stateHistoryEntries > 0 ||
                reportedIncidents > 0 ||
                resolvedIncidents > 0 ||
                cancellations > 0 ||
                courierSettlements > 0 ||
                hasBusiness);

        UserReferencesDto references = UserReferencesDto.builder()
                .userId(userId)
                .hasReferences(hasReferences)
                .trackingGuidesAsCourier(trackingGuidesAsCourier)
                .activeGuidesAsCourier(activeGuidesAsCourier)
                .trackingGuidesAsCoordinator(trackingGuidesAsCoordinator)
                .activeGuidesAsCoordinator(activeGuidesAsCoordinator)
                .stateHistoryEntries(stateHistoryEntries)
                .reportedIncidents(reportedIncidents)
                .resolvedIncidents(resolvedIncidents)
                .cancellations(cancellations)
                .courierSettlements(courierSettlements)
                .pendingSettlements(pendingSettlements)
                .hasBusiness(hasBusiness)
                .businessName(businessName)
                .auditLogEntries(auditLogEntries)
                .build();

        log.info("User {} has references: {}", userId, hasReferences);

        return references;
    }
}
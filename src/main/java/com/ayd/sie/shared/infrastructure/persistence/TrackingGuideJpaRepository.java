package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.TrackingGuide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IMPORTANT: This is the complete TrackingGuideJpaRepository interface
 * Replace the existing one with this version that includes all necessary
 * methods
 */
@Repository
public interface TrackingGuideJpaRepository
                extends JpaRepository<TrackingGuide, Integer>, JpaSpecificationExecutor<TrackingGuide> {

        Optional<TrackingGuide> findByGuideNumber(String guideNumber);

        List<TrackingGuide> findByBusinessBusinessId(Integer businessId);

        List<TrackingGuide> findByOriginBranchBranchId(Integer branchId);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.business.businessId = :businessId AND tg.currentState.isFinal = false")
        List<TrackingGuide> findActiveByBusinessId(@Param("businessId") Integer businessId);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.originBranch.branchId = :branchId AND tg.currentState.isFinal = false")
        List<TrackingGuide> findActiveByOriginBranchId(@Param("branchId") Integer branchId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.business.businessId = :businessId")
        long countByBusinessId(@Param("businessId") Integer businessId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.originBranch.branchId = :branchId")
        long countByOriginBranchId(@Param("branchId") Integer branchId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.business.businessId = :businessId AND tg.currentState.isFinal = false")
        long countActiveByBusinessId(@Param("businessId") Integer businessId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.originBranch.branchId = :branchId AND tg.currentState.isFinal = false")
        long countActiveByOriginBranchId(@Param("branchId") Integer branchId);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.courier.userId = :courierId")
        Page<TrackingGuide> findByCourierId(@Param("courierId") Integer courierId, Pageable pageable);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.courier.userId = :courierId")
        long countAssignedToCourier(@Param("courierId") Integer courierId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.courier.userId = :courierId " +
                        "AND tg.currentState.stateName = 'Entregada'")
        long countCompletedByCourier(@Param("courierId") Integer courierId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.courier.userId = :courierId " +
                        "AND tg.currentState.stateName IN ('Asignada', 'Recogida', 'En Ruta')")
        long countPendingByCourier(@Param("courierId") Integer courierId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.courier.userId = :courierId " +
                        "AND tg.currentState.stateName = 'Incidencia'")
        long countIncidentsByCourier(@Param("courierId") Integer courierId);

        // State-based counts
        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.currentState.stateName = :stateName")
        long countByStateName(@Param("stateName") String stateName);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.currentState.stateName = :stateName " +
                        "AND DATE(tg.createdAt) = CURRENT_DATE")
        long countByStateNameToday(@Param("stateName") String stateName);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.currentState.stateName = :stateName " +
                        "AND tg.createdAt BETWEEN :startDate AND :endDate")
        long countByStateNameAndDateRange(@Param("stateName") String stateName,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Business-related counts
        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.business.businessId = :businessId " +
                        "AND tg.currentState.stateName = 'Cancelada'")
        long countCancelledByBusinessId(@Param("businessId") Integer businessId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.business.businessId = :businessId " +
                        "AND tg.currentState.stateName = 'Entregada'")
        long countCompletedByBusinessId(@Param("businessId") Integer businessId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.business.businessId = :businessId " +
                        "AND tg.createdAt BETWEEN :startDate AND :endDate")
        long countByBusinessIdAndDateRange(@Param("businessId") Integer businessId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Coordinator workload
        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.coordinator.userId = :coordinatorId")
        long countAssignedByCoordinator(@Param("coordinatorId") Integer coordinatorId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.coordinator.userId = :coordinatorId " +
                        "AND DATE(tg.createdAt) = CURRENT_DATE")
        long countAssignedByCoordinatorToday(@Param("coordinatorId") Integer coordinatorId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.coordinator.userId = :coordinatorId " +
                        "AND tg.createdAt BETWEEN :startDate AND :endDate")
        long countAssignedByCoordinatorAndDateRange(@Param("coordinatorId") Integer coordinatorId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Advanced filtering
        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.currentState.stateName IN :stateNames")
        long countByStateNames(@Param("stateNames") List<String> stateNames);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.courier.userId = :courierId " +
                        "AND tg.currentState.stateName IN :stateNames")
        long countByCourierAndStateNames(@Param("courierId") Integer courierId,
                        @Param("stateNames") List<String> stateNames);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.business.businessId = :businessId " +
                        "AND tg.currentState.stateName IN :stateNames")
        long countByBusinessAndStateNames(@Param("businessId") Integer businessId,
                        @Param("stateNames") List<String> stateNames);

        // Pending assignments (created but not assigned)
        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.currentState.stateName = 'Creada' ORDER BY tg.createdAt ASC")
        Page<TrackingGuide> findPendingAssignments(Pageable pageable);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.currentState.stateName = 'Creada'")
        long countPendingAssignments();

        // Guides by multiple states
        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.currentState.stateName IN :stateNames ORDER BY tg.createdAt DESC")
        Page<TrackingGuide> findByStateNames(@Param("stateNames") List<String> stateNames, Pageable pageable);

        // Recent guides by state
        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.currentState.stateName = :stateName " +
                        "ORDER BY tg.createdAt DESC")
        Page<TrackingGuide> findRecentByStateName(@Param("stateName") String stateName, Pageable pageable);

        // Guides with incidents
        @Query("SELECT DISTINCT tg FROM TrackingGuide tg " +
                        "JOIN DeliveryIncident di ON di.guide.guideId = tg.guideId " +
                        "WHERE di.resolved = :resolved ORDER BY tg.createdAt DESC")
        Page<TrackingGuide> findGuidesWithIncidents(@Param("resolved") Boolean resolved, Pageable pageable);

        // Assignment acceptance status
        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.courier.userId = :courierId " +
                        "AND tg.assignmentAccepted = :accepted")
        long countByCourierAndAcceptanceStatus(@Param("courierId") Integer courierId,
                        @Param("accepted") Boolean accepted);

        // Time-based queries
        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.createdAt BETWEEN :startDate AND :endDate " +
                        "ORDER BY tg.createdAt DESC")
        Page<TrackingGuide> findByDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.deliveryDate BETWEEN :startDate AND :endDate " +
                        "ORDER BY tg.deliveryDate ASC")
        Page<TrackingGuide> findByDeliveryDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

        // Search functionality
        @Query("SELECT tg FROM TrackingGuide tg WHERE " +
                        "LOWER(tg.guideNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(tg.recipientName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(tg.recipientAddress) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(tg.business.businessName) LIKE LOWER(CONCAT('%', :search, '%'))")
        Page<TrackingGuide> searchGuides(@Param("search") String search, Pageable pageable);

        // Efficiency metrics
        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.currentState.isFinal = true")
        long countCompletedDeliveries();

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.currentState.isFinal = false")
        long countActiveDeliveries();

        @Query("SELECT AVG(TIMESTAMPDIFF(HOUR, tg.createdAt, tg.deliveryDate)) FROM TrackingGuide tg " +
                        "WHERE tg.currentState.stateName = 'Entregada' AND tg.deliveryDate IS NOT NULL")
        Double getAverageDeliveryTimeInHours();

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.courier.userId = :courierId")
        long countByCourierId(@Param("courierId") Integer courierId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.courier.userId = :courierId AND tg.currentState.isFinal = false")
        long countActiveByCourierId(@Param("courierId") Integer courierId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.coordinator.userId = :coordinatorId")
        long countByCoordinatorId(@Param("coordinatorId") Integer coordinatorId);

        @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.coordinator.userId = :coordinatorId AND tg.currentState.isFinal = false")
        long countActiveByCoordinatorId(@Param("coordinatorId") Integer coordinatorId);

        // Additional methods for courier module
        Page<TrackingGuide> findByCourierUserId(Integer courierId, Pageable pageable);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.courier.userId = :courierId AND tg.currentState.stateName IN :stateNames ORDER BY tg.createdAt ASC")
        List<TrackingGuide> findByCourierUserIdAndCurrentStateStateNameInOrderByCreatedAtAsc(
                        @Param("courierId") Integer courierId, @Param("stateNames") List<String> stateNames);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.courier.userId = :courierId AND tg.currentState.stateName = :stateName ORDER BY tg.createdAt ASC")
        List<TrackingGuide> findByCourierUserIdAndCurrentStateStateNameOrderByCreatedAtAsc(
                        @Param("courierId") Integer courierId, @Param("stateName") String stateName);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.courier.userId = :courierId AND tg.currentState.stateName = :stateName AND tg.deliveryDate BETWEEN :startDate AND :endDate")
        Page<TrackingGuide> findByCourierUserIdAndCurrentStateStateNameAndDeliveryDateBetween(
                        @Param("courierId") Integer courierId, @Param("stateName") String stateName,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.courier.userId = :courierId AND tg.currentState.stateName = :stateName AND tg.deliveryDate >= :startDate")
        Page<TrackingGuide> findByCourierUserIdAndCurrentStateStateNameAndDeliveryDateGreaterThanEqual(
                        @Param("courierId") Integer courierId, @Param("stateName") String stateName,
                        @Param("startDate") LocalDateTime startDate, Pageable pageable);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.courier.userId = :courierId AND tg.currentState.stateName = :stateName AND tg.deliveryDate <= :endDate")
        Page<TrackingGuide> findByCourierUserIdAndCurrentStateStateNameAndDeliveryDateLessThanEqual(
                        @Param("courierId") Integer courierId, @Param("stateName") String stateName,
                        @Param("endDate") LocalDateTime endDate, Pageable pageable);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.courier.userId = :courierId AND tg.currentState.stateName = :stateName")
        Page<TrackingGuide> findByCourierUserIdAndCurrentStateStateName(@Param("courierId") Integer courierId,
                        @Param("stateName") String stateName, Pageable pageable);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.courier.userId = :courierId AND tg.currentState.stateName = :stateName AND tg.deliveryDate BETWEEN :startDate AND :endDate")
        List<TrackingGuide> findByCourierUserIdAndCurrentStateStateNameAndDeliveryDateBetween(
                        @Param("courierId") Integer courierId, @Param("stateName") String stateName,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.courier.userId = :courierId AND tg.currentState.stateName = :stateName AND tg.deliveryDate >= :startDate")
        List<TrackingGuide> findByCourierUserIdAndCurrentStateStateNameAndDeliveryDateGreaterThanEqual(
                        @Param("courierId") Integer courierId, @Param("stateName") String stateName,
                        @Param("startDate") LocalDateTime startDate);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.courier.userId = :courierId AND tg.currentState.stateName = :stateName AND tg.deliveryDate <= :endDate")
        List<TrackingGuide> findByCourierUserIdAndCurrentStateStateNameAndDeliveryDateLessThanEqual(
                        @Param("courierId") Integer courierId, @Param("stateName") String stateName,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT tg FROM TrackingGuide tg WHERE tg.courier.userId = :courierId AND tg.currentState.stateName = :stateName")
        List<TrackingGuide> findByCourierUserIdAndCurrentStateStateName(@Param("courierId") Integer courierId,
                        @Param("stateName") String stateName);
}
package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessJpaRepository extends JpaRepository<Business, Integer> {

        Optional<Business> findByUserUserIdAndActiveTrue(Integer userId);

        // List<Business> findByActiveTrue();

        // Page<Business> findByActiveTrue(Pageable pageable);

        boolean existsByTaxIdAndBusinessIdNotAndActiveTrue(String taxId, Integer businessId);

        @Query("SELECT b FROM Business b JOIN b.currentLevel l WHERE b.active = true AND l.levelId = :levelId")
        List<Business> findByLoyaltyLevel(@Param("levelId") Integer levelId);

        Optional<Business> findByTaxIdAndActiveTrue(String taxId);

        Optional<Business> findByUserUserId(Integer userId);

        @Query("SELECT b FROM Business b WHERE b.active = true ORDER BY b.businessName")
        Page<Business> findAllActive(Pageable pageable);

        @Query("SELECT b FROM Business b WHERE " +
                        "(LOWER(b.businessName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(b.legalName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(b.taxId) LIKE LOWER(CONCAT('%', :search, '%')))")
        Page<Business> findBySearch(@Param("search") String search, Pageable pageable);

        @Query("SELECT b FROM Business b WHERE b.currentLevel.levelId = :levelId")
        List<Business> findByCurrentLevelId(@Param("levelId") Integer levelId);

        boolean existsByTaxIdAndActiveTrue(String taxId);

        boolean existsByBusinessEmailAndActiveTrue(String businessEmail);

        // Count businesses by loyalty level
        @Query("SELECT COUNT(b) FROM Business b WHERE b.currentLevel.levelId = :levelId")
        long countByCurrentLevelLevelId(@Param("levelId") Integer levelId);

        // Additional methods for reporting
        @Query("SELECT b.businessId, b.businessName, b.businessEmail, " +
                        "COALESCE(ll.levelName, 'None') as loyaltyLevel, " +
                        "COUNT(tg.guideId) as totalDeliveries, " +
                        "SUM(CASE WHEN tg.currentState.stateName = 'Entregada' THEN 1 ELSE 0 END) as completedDeliveries, "
                        +
                        "SUM(CASE WHEN tg.currentState.stateName IN ('Cancelada', 'Rechazada') THEN 1 ELSE 0 END) as cancelledDeliveries, "
                        +
                        "COALESCE(SUM(tg.basePrice), 0) as totalRevenue " +
                        "FROM Business b " +
                        "LEFT JOIN b.currentLevel ll " +
                        "LEFT JOIN TrackingGuide tg ON b.businessId = tg.business.businessId " +
                        "AND tg.createdAt BETWEEN :startDate AND :endDate " +
                        "WHERE b.active = true " +
                        "GROUP BY b.businessId, b.businessName, b.businessEmail, ll.levelName " +
                        "ORDER BY totalDeliveries DESC")
        List<Object[]> findBusinessStatisticsForPeriod(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT b.businessId, b.businessName, b.businessEmail, " +
                        "COALESCE(ll.levelName, 'None') as loyaltyLevel, " +
                        "COUNT(tg.guideId) as totalDeliveries, " +
                        "SUM(CASE WHEN tg.currentState.stateName = 'Entregada' THEN 1 ELSE 0 END) as completedDeliveries, "
                        +
                        "SUM(CASE WHEN tg.currentState.stateName IN ('Cancelada', 'Rechazada') THEN 1 ELSE 0 END) as cancelledDeliveries "
                        +
                        "FROM Business b " +
                        "LEFT JOIN b.currentLevel ll " +
                        "LEFT JOIN TrackingGuide tg ON b.businessId = tg.business.businessId " +
                        "AND tg.currentState.stateName IN ('Cancelada', 'Rechazada') " +
                        "AND tg.cancellationDate BETWEEN :startDate AND :endDate " +
                        "WHERE b.active = true " +
                        "GROUP BY b.businessId, b.businessName, b.businessEmail, ll.levelName " +
                        "HAVING SUM(CASE WHEN tg.currentState.stateName IN ('Cancelada', 'Rechazada') THEN 1 ELSE 0 END) > 0 "
                        +
                        "ORDER BY cancelledDeliveries DESC")
        List<Object[]> findCancellationStatisticsByBusiness(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}
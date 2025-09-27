package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.Cancellation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CancellationJpaRepository extends JpaRepository<Cancellation, Integer> {

    @Query("SELECT COUNT(c) FROM Cancellation c WHERE c.cancelledByUser.userId = :userId")
    long countByCancelledByUserId(@Param("userId") Integer userId);

    @Query("SELECT c FROM Cancellation c WHERE c.guide.guideId = :guideId")
    Optional<Cancellation> findByGuideId(@Param("guideId") Integer guideId);

    @Query("SELECT c FROM Cancellation c WHERE c.requestedByUser.userId = :userId")
    List<Cancellation> findByRequestedByUserId(@Param("userId") Integer userId);

    @Query("SELECT c FROM Cancellation c WHERE c.processedByUser.userId = :userId")
    List<Cancellation> findByProcessedByUserId(@Param("userId") Integer userId);

    @Query("SELECT c FROM Cancellation c WHERE c.guide.business.businessId = :businessId")
    List<Cancellation> findByBusinessId(@Param("businessId") Integer businessId);

    @Query("SELECT COUNT(c) FROM Cancellation c WHERE c.guide.business.businessId = :businessId " +
            "AND c.processedAt BETWEEN :startDate AND :endDate")
    long countByBusinessIdAndDateRange(@Param("businessId") Integer businessId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(c) FROM Cancellation c WHERE c.cancellationType.cancellationTypeId = :typeId " +
            "AND c.processedAt BETWEEN :startDate AND :endDate")
    long countByTypeAndDateRange(@Param("typeId") Integer typeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(c.commissionPenalty) FROM Cancellation c WHERE c.guide.courier.userId = :courierId " +
            "AND c.processedAt BETWEEN :startDate AND :endDate")
    Double sumPenaltiesByCourierAndDateRange(@Param("courierId") Integer courierId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
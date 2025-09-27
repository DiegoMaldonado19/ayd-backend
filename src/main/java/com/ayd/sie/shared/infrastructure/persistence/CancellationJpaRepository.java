package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.Cancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CancellationJpaRepository
                extends JpaRepository<Cancellation, Integer>, JpaSpecificationExecutor<Cancellation> {

        Optional<Cancellation> findByGuideGuideId(Integer guideId);

        List<Cancellation> findByCancelledByUserUserId(Integer userId);

        @Query("SELECT c FROM Cancellation c WHERE c.guide.business.businessId = :businessId ORDER BY c.cancelledAt DESC")
        List<Cancellation> findByBusinessId(@Param("businessId") Integer businessId);

        @Query("SELECT c FROM Cancellation c WHERE c.cancelledAt BETWEEN :startDate AND :endDate ORDER BY c.cancelledAt DESC")
        List<Cancellation> findByCancelledAtBetween(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT COUNT(c) FROM Cancellation c WHERE c.guide.business.businessId = :businessId")
        long countByBusinessId(@Param("businessId") Integer businessId);

        @Query("SELECT COUNT(c) FROM Cancellation c WHERE c.cancelledByUser.userId = :userId")
        long countByCancelledByUserId(@Param("userId") Integer userId);

        @Query("SELECT COUNT(c) FROM Cancellation c WHERE c.cancelledAt BETWEEN :startDate AND :endDate")
        long countByCancelledAtBetween(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}
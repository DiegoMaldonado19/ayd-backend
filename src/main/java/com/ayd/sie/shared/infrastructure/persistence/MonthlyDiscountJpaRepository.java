package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.MonthlyDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyDiscountJpaRepository extends JpaRepository<MonthlyDiscount, Integer> {

    List<MonthlyDiscount> findByBusinessBusinessId(Integer businessId);

    Optional<MonthlyDiscount> findByBusinessBusinessIdAndYearAndMonth(Integer businessId, Integer year, Integer month);

    @Query("SELECT md FROM MonthlyDiscount md WHERE md.year = :year AND md.month = :month")
    List<MonthlyDiscount> findByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query("SELECT md FROM MonthlyDiscount md WHERE md.calculatedAt BETWEEN :startDate AND :endDate")
    List<MonthlyDiscount> findByCalculatedAtBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT md FROM MonthlyDiscount md WHERE md.business.businessId = :businessId AND md.calculatedAt BETWEEN :startDate AND :endDate ORDER BY md.year DESC, md.month DESC")
    List<MonthlyDiscount> findByBusinessIdAndDateRange(@Param("businessId") Integer businessId,
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
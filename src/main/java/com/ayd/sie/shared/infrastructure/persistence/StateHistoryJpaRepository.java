package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.StateHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StateHistoryJpaRepository extends JpaRepository<StateHistory, Integer> {

    List<StateHistory> findByGuideGuideIdOrderByChangedAtDesc(Integer guideId);

    @Query("SELECT COUNT(sh) FROM StateHistory sh WHERE sh.user.userId = :userId")
    long countByUserId(@Param("userId") Integer userId);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.guide.guideId = :guideId ORDER BY sh.changedAt DESC")
    List<StateHistory> findByGuideIdOrderByChangedAtDesc(@Param("guideId") Integer guideId);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.guide.guideId = :guideId ORDER BY sh.changedAt ASC")
    List<StateHistory> findByGuideIdOrderByChangedAtAsc(@Param("guideId") Integer guideId);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.guide.guideNumber = :guideNumber ORDER BY sh.changedAt DESC")
    List<StateHistory> findByGuideNumberOrderByChangedAtDesc(@Param("guideNumber") String guideNumber);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.user.userId = :userId ORDER BY sh.changedAt DESC")
    Page<StateHistory> findByUserIdOrderByChangedAtDesc(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.state.stateId = :stateId ORDER BY sh.changedAt DESC")
    Page<StateHistory> findByStateIdOrderByChangedAtDesc(@Param("stateId") Integer stateId, Pageable pageable);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.state.stateName = :stateName ORDER BY sh.changedAt DESC")
    Page<StateHistory> findByStateNameOrderByChangedAtDesc(@Param("stateName") String stateName, Pageable pageable);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.changedAt BETWEEN :startDate AND :endDate ORDER BY sh.changedAt DESC")
    Page<StateHistory> findByChangedAtBetweenOrderByChangedAtDesc(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.guide.business.businessId = :businessId ORDER BY sh.changedAt DESC")
    Page<StateHistory> findByBusinessIdOrderByChangedAtDesc(@Param("businessId") Integer businessId, Pageable pageable);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.guide.courier.userId = :courierId ORDER BY sh.changedAt DESC")
    Page<StateHistory> findByCourierIdOrderByChangedAtDesc(@Param("courierId") Integer courierId, Pageable pageable);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.guide.coordinator.userId = :coordinatorId ORDER BY sh.changedAt DESC")
    Page<StateHistory> findByCoordinatorIdOrderByChangedAtDesc(@Param("coordinatorId") Integer coordinatorId,
            Pageable pageable);

    @Query("SELECT COUNT(sh) FROM StateHistory sh WHERE sh.state.stateName = :stateName")
    long countByStateName(@Param("stateName") String stateName);

    @Query("SELECT COUNT(sh) FROM StateHistory sh WHERE sh.changedAt BETWEEN :startDate AND :endDate")
    long countByChangedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.guide.guideId = :guideId AND sh.state.stateName = :stateName ORDER BY sh.changedAt DESC")
    List<StateHistory> findByGuideIdAndStateName(@Param("guideId") Integer guideId,
            @Param("stateName") String stateName);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.guide.guideId = :guideId AND sh.changedAt = " +
            "(SELECT MAX(sh2.changedAt) FROM StateHistory sh2 WHERE sh2.guide.guideId = :guideId)")
    StateHistory findLatestByGuideId(@Param("guideId") Integer guideId);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.user.userId = :userId AND sh.changedAt BETWEEN :startDate AND :endDate ORDER BY sh.changedAt DESC")
    Page<StateHistory> findByUserIdAndDateRangeOrderByChangedAtDesc(@Param("userId") Integer userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT sh FROM StateHistory sh WHERE sh.guide.business.businessId = :businessId " +
            "AND sh.changedAt BETWEEN :startDate AND :endDate ORDER BY sh.changedAt DESC")
    Page<StateHistory> findByBusinessIdAndDateRangeOrderByChangedAtDesc(@Param("businessId") Integer businessId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT DISTINCT sh.state FROM StateHistory sh WHERE sh.guide.guideId = :guideId ORDER BY sh.changedAt")
    List<com.ayd.sie.shared.domain.entities.TrackingState> findDistinctStatesByGuideId(
            @Param("guideId") Integer guideId);
}
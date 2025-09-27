package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.TrackingGuide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingGuideJpaRepository extends JpaRepository<TrackingGuide, Integer> {

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

    @Query("SELECT tg FROM TrackingGuide tg WHERE tg.currentState.stateId = :stateId")
    List<TrackingGuide> findByCurrentStateId(@Param("stateId") Integer stateId);

    boolean existsByGuideNumber(String guideNumber);
}
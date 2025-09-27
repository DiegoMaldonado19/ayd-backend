package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.TrackingState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingStateJpaRepository extends JpaRepository<TrackingState, Integer> {

    @Query("SELECT ts FROM TrackingState ts WHERE ts.stateName = :stateName")
    Optional<TrackingState> findByStateName(@Param("stateName") String stateName);

    @Query("SELECT ts FROM TrackingState ts WHERE ts.active = true ORDER BY ts.stateOrder")
    List<TrackingState> findAllActiveOrderByStateOrder();

    @Query("SELECT ts FROM TrackingState ts WHERE ts.isFinal = :isFinal AND ts.active = true ORDER BY ts.stateOrder")
    List<TrackingState> findByIsFinalAndActiveTrue(@Param("isFinal") Boolean isFinal);

    @Query("SELECT ts FROM TrackingState ts WHERE ts.isFinal = false AND ts.active = true ORDER BY ts.stateOrder")
    List<TrackingState> findAllNonFinalStates();

    @Query("SELECT ts FROM TrackingState ts WHERE ts.isFinal = true AND ts.active = true ORDER BY ts.stateOrder")
    List<TrackingState> findAllFinalStates();

    @Query("SELECT ts FROM TrackingState ts WHERE ts.stateOrder > :currentOrder AND ts.active = true ORDER BY ts.stateOrder")
    List<TrackingState> findNextStates(@Param("currentOrder") Integer currentOrder);

    @Query("SELECT ts FROM TrackingState ts WHERE ts.stateOrder < :currentOrder AND ts.active = true ORDER BY ts.stateOrder DESC")
    List<TrackingState> findPreviousStates(@Param("currentOrder") Integer currentOrder);

    @Query("SELECT ts FROM TrackingState ts WHERE ts.stateOrder BETWEEN :minOrder AND :maxOrder AND ts.active = true ORDER BY ts.stateOrder")
    List<TrackingState> findStatesByOrderRange(@Param("minOrder") Integer minOrder,
            @Param("maxOrder") Integer maxOrder);

    boolean existsByStateNameAndActiveTrue(String stateName);

    @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.currentState.stateId = :stateId")
    long countGuidesByStateId(@Param("stateId") Integer stateId);

    @Query("SELECT COUNT(tg) FROM TrackingGuide tg WHERE tg.currentState.stateName = :stateName")
    long countGuidesByStateName(@Param("stateName") String stateName);

    @Query("SELECT ts FROM TrackingState ts WHERE ts.stateName IN :stateNames AND ts.active = true")
    List<TrackingState> findByStateNamesAndActiveTrue(@Param("stateNames") List<String> stateNames);

    @Query("SELECT ts FROM TrackingState ts WHERE ts.stateName LIKE %:namePattern% AND ts.active = true ORDER BY ts.stateOrder")
    List<TrackingState> findByStateNameContainingAndActiveTrue(@Param("namePattern") String namePattern);
}
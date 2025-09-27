package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.LoyaltyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyLevelJpaRepository extends JpaRepository<LoyaltyLevel, Integer> {

    List<LoyaltyLevel> findByActiveTrueOrderByMinDeliveries();

    Optional<LoyaltyLevel> findByLevelNameAndActiveTrue(String levelName);

    @Query("SELECT l FROM LoyaltyLevel l WHERE l.active = true AND " +
            ":deliveries >= l.minDeliveries AND " +
            "(l.maxDeliveries IS NULL OR :deliveries <= l.maxDeliveries) " +
            "ORDER BY l.minDeliveries DESC LIMIT 1")
    Optional<LoyaltyLevel> findLevelByDeliveryCount(@Param("deliveries") Integer deliveries);

    boolean existsByLevelNameAndActiveTrue(String levelName);

    boolean existsByLevelNameAndLevelIdNotAndActiveTrue(String levelName, Integer levelId);
}
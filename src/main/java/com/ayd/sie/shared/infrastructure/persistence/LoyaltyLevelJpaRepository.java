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

    List<LoyaltyLevel> findByActiveTrueOrderByLevelName();

    Optional<LoyaltyLevel> findByLevelNameAndActiveTrue(String levelName);

    boolean existsByLevelNameAndActiveTrue(String levelName);

    @Query("SELECT ll FROM LoyaltyLevel ll WHERE ll.active = :active ORDER BY ll.levelName")
    List<LoyaltyLevel> findByActive(@Param("active") Boolean active);

    @Query("SELECT COUNT(ll) FROM LoyaltyLevel ll WHERE ll.active = true")
    long countActiveLevels();

    @Query("SELECT ll FROM LoyaltyLevel ll WHERE ll.minDeliveries <= :deliveryCount AND ll.active = true ORDER BY ll.minDeliveries DESC")
    List<LoyaltyLevel> findEligibleLevels(@Param("deliveryCount") Integer deliveryCount);

    Optional<LoyaltyLevel> findByLevelName(String levelName);

    List<LoyaltyLevel> findByActiveTrueOrderByMinDeliveries();

    @Query("SELECT ll FROM LoyaltyLevel ll WHERE ll.minDeliveries <= :deliveries ORDER BY ll.minDeliveries DESC")
    List<LoyaltyLevel> findApplicableLevels(@Param("deliveries") Integer deliveries);

    boolean existsByLevelName(String levelName);
}
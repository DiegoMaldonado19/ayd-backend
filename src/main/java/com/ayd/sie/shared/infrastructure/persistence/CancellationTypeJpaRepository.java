package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.CancellationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CancellationTypeJpaRepository extends JpaRepository<CancellationType, Integer> {

    List<CancellationType> findByActiveTrueOrderByTypeName();

    Optional<CancellationType> findByTypeNameAndActiveTrue(String typeName);

    boolean existsByTypeName(String typeName);

    @Query("SELECT ct FROM CancellationType ct WHERE ct.active = :active ORDER BY ct.typeName")
    List<CancellationType> findByActive(@Param("active") Boolean active);

    @Query("SELECT COUNT(ct) FROM CancellationType ct WHERE ct.active = true")
    long countActiveTypes();
}
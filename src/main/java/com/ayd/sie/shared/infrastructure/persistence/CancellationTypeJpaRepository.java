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

    @Query("SELECT ct FROM CancellationType ct WHERE ct.active = true ORDER BY ct.typeName")
    List<CancellationType> findAllActive();

    @Query("SELECT ct FROM CancellationType ct WHERE ct.typeName = :typeName AND ct.active = true")
    Optional<CancellationType> findByTypeNameAndActiveTrue(@Param("typeName") String typeName);

    boolean existsByTypeNameAndActiveTrue(String typeName);
}
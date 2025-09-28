package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.IncidentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncidentTypeJpaRepository extends JpaRepository<IncidentType, Integer> {

    @Query("SELECT it FROM IncidentType it WHERE it.active = true ORDER BY it.typeName")
    List<IncidentType> findAllActive();

    @Query("SELECT it FROM IncidentType it WHERE it.typeName = :typeName AND it.active = true")
    Optional<IncidentType> findByTypeNameAndActiveTrue(@Param("typeName") String typeName);

    @Query("SELECT it FROM IncidentType it WHERE it.requiresReturn = :requiresReturn AND it.active = true")
    List<IncidentType> findByRequiresReturnAndActiveTrue(@Param("requiresReturn") Boolean requiresReturn);

    boolean existsByTypeNameAndActiveTrue(String typeName);
}
package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.EvidenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvidenceTypeJpaRepository extends JpaRepository<EvidenceType, Integer> {

    @Query("SELECT et FROM EvidenceType et WHERE et.active = true ORDER BY et.typeName")
    List<EvidenceType> findAllActiveOrderByTypeName();

    @Query("SELECT et FROM EvidenceType et WHERE et.typeName = :typeName AND et.active = true")
    Optional<EvidenceType> findByTypeNameAndActiveTrue(@Param("typeName") String typeName);

    @Query("SELECT et FROM EvidenceType et WHERE et.evidenceTypeId = :id AND et.active = true")
    Optional<EvidenceType> findByIdAndActiveTrue(@Param("id") Integer id);
}
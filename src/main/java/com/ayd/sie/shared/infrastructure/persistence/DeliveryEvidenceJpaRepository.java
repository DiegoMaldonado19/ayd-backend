package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.DeliveryEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryEvidenceJpaRepository
        extends JpaRepository<DeliveryEvidence, Integer>, JpaSpecificationExecutor<DeliveryEvidence> {

    @Query("SELECT de FROM DeliveryEvidence de WHERE de.guide.guideId = :guideId ORDER BY de.createdAt ASC")
    List<DeliveryEvidence> findByGuideIdOrderByCreatedAtAsc(@Param("guideId") Integer guideId);

    @Query("SELECT de FROM DeliveryEvidence de WHERE de.guide.guideId = :guideId AND de.evidenceTypeId = :evidenceTypeId")
    List<DeliveryEvidence> findByGuideIdAndEvidenceTypeId(@Param("guideId") Integer guideId,
            @Param("evidenceTypeId") Integer evidenceTypeId);

    @Query("SELECT COUNT(de) FROM DeliveryEvidence de WHERE de.guide.guideId = :guideId")
    long countByGuideId(@Param("guideId") Integer guideId);

    @Query("SELECT de FROM DeliveryEvidence de WHERE de.guide.courier.userId = :courierId ORDER BY de.createdAt DESC")
    List<DeliveryEvidence> findByCourierIdOrderByCreatedAtDesc(@Param("courierId") Integer courierId);
}
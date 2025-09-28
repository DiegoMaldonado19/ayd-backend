package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.DeliveryIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryIncidentJpaRepository
        extends JpaRepository<DeliveryIncident, Integer>, JpaSpecificationExecutor<DeliveryIncident> {

    @Query("SELECT COUNT(di) FROM DeliveryIncident di WHERE di.reportedByUser.userId = :userId")
    long countByReportedByUserId(@Param("userId") Integer userId);

    @Query("SELECT COUNT(di) FROM DeliveryIncident di WHERE di.resolvedByUser.userId = :userId")
    long countByResolvedByUserId(@Param("userId") Integer userId);

    @Query("SELECT CASE WHEN COUNT(di) > 0 THEN true ELSE false END FROM DeliveryIncident di WHERE di.guide.guideId = :guideId")
    boolean existsByGuideId(@Param("guideId") Integer guideId);
}
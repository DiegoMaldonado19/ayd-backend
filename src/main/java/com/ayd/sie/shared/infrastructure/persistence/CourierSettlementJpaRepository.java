package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.CourierSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierSettlementJpaRepository extends JpaRepository<CourierSettlement, Integer> {

    @Query("SELECT COUNT(cs) FROM CourierSettlement cs WHERE cs.courier.userId = :courierId")
    long countByCourierId(@Param("courierId") Integer courierId);

    @Query("SELECT COUNT(cs) FROM CourierSettlement cs WHERE cs.courier.userId = :courierId AND cs.status.statusId != 3")
    long countPendingByCourierId(@Param("courierId") Integer courierId);
}
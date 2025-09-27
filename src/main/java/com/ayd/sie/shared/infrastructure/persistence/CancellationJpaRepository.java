package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.Cancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CancellationJpaRepository extends JpaRepository<Cancellation, Integer> {

    @Query("SELECT COUNT(c) FROM Cancellation c WHERE c.cancelledByUser.userId = :userId")
    long countByCancelledByUserId(@Param("userId") Integer userId);
}
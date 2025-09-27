package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.StateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateHistoryJpaRepository extends JpaRepository<StateHistory, Integer> {

    List<StateHistory> findByGuideGuideIdOrderByChangedAtDesc(Integer guideId);

    @Query("SELECT COUNT(sh) FROM StateHistory sh WHERE sh.user.userId = :userId")
    long countByUserId(@Param("userId") Integer userId);
}
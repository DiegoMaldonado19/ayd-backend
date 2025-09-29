package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationJpaRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByGuideGuideIdOrderByCreatedAtDesc(Integer guideId);

    List<Notification> findByUserUserIdOrderByCreatedAtDesc(Integer userId);

    List<Notification> findBySentFalse();

    @Query("SELECT n FROM Notification n WHERE n.guide.guideNumber = :guideNumber ORDER BY n.createdAt DESC")
    List<Notification> findByGuideNumber(@Param("guideNumber") String guideNumber);
}
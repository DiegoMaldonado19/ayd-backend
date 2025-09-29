package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTypeJpaRepository extends JpaRepository<NotificationType, Integer> {

    Optional<NotificationType> findByTypeName(String typeName);

    Optional<NotificationType> findByTypeNameAndActiveTrue(String typeName);
}
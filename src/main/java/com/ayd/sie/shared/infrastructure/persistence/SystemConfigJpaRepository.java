package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigJpaRepository extends JpaRepository<SystemConfig, Integer> {

    Optional<SystemConfig> findByConfigKey(String configKey);

    List<SystemConfig> findAllByOrderByConfigKey();

    boolean existsByConfigKey(String configKey);
}
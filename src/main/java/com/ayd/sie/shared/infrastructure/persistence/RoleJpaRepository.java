package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleJpaRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByRoleNameAndActiveTrue(String roleName);

    List<Role> findByActiveTrue();

    boolean existsByRoleName(String roleName);
}
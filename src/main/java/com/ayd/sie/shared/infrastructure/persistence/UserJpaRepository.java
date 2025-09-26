package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmailAndActiveTrue(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByPasswordResetToken(String passwordResetToken);

    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);

    List<User> findByRoleRoleIdAndActiveTrue(Integer roleId);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true AND " +
            "(u.lockedUntil IS NULL OR u.lockedUntil < :now)")
    Optional<User> findActiveUnlockedUserByEmail(@Param("email") String email, @Param("now") LocalDateTime now);

    @Query("SELECT u FROM User u WHERE u.role.roleId = :roleId AND u.active = true")
    List<User> findActiveUsersByRole(@Param("roleId") Integer roleId);
}
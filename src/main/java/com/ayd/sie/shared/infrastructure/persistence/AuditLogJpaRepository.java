package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLog, Integer> {

    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    Page<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.tableName = :tableName ORDER BY a.createdAt DESC")
    Page<AuditLog> findByTableName(@Param("tableName") String tableName, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.user.userId = :userId ORDER BY a.createdAt DESC")
    Page<AuditLog> findByUserId(@Param("userId") Integer userId, Pageable pageable);

    List<String> findDistinctTableNameByOrderByTableName();

    // Count entries by user
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.user.userId = :userId")
    long countByUserId(@Param("userId") Integer userId);

    // Update user_id to NULL for a specific user
    @Modifying
    @Query("UPDATE AuditLog a SET a.user = NULL WHERE a.user.userId = :userId")
    int setUserToNullByUserId(@Param("userId") Integer userId);
}
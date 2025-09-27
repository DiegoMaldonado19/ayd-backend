package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchJpaRepository extends JpaRepository<Branch, Integer> {

    Optional<Branch> findByBranchCodeAndActiveTrue(String branchCode);

    List<Branch> findByActiveTrue();

    Page<Branch> findByActiveTrue(Pageable pageable);

    @Query("SELECT b FROM Branch b WHERE b.active = true AND " +
            "(LOWER(b.branchName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(b.branchCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(b.city) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Branch> findActiveBySearch(@Param("search") String search, Pageable pageable);

    boolean existsByBranchCodeAndActiveTrue(String branchCode);

    boolean existsByBranchCodeAndBranchIdNotAndActiveTrue(String branchCode, Integer branchId);
}
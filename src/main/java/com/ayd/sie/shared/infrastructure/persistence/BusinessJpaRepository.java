package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessJpaRepository extends JpaRepository<Business, Integer> {

    Optional<Business> findByUserUserIdAndActiveTrue(Integer userId);

    Optional<Business> findByTaxIdAndActiveTrue(String taxId);

    List<Business> findByActiveTrue();

    Page<Business> findByActiveTrue(Pageable pageable);

    @Query("SELECT b FROM Business b WHERE b.active = true AND " +
            "(LOWER(b.businessName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(b.legalName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(b.taxId) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Business> findActiveBySearch(@Param("search") String search, Pageable pageable);

    boolean existsByTaxIdAndActiveTrue(String taxId);

    boolean existsByTaxIdAndBusinessIdNotAndActiveTrue(String taxId, Integer businessId);

    @Query("SELECT b FROM Business b JOIN b.currentLevel l WHERE b.active = true AND l.levelId = :levelId")
    List<Business> findByLoyaltyLevel(@Param("levelId") Integer levelId);
}
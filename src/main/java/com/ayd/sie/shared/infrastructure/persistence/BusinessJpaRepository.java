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

    List<Business> findByActiveTrue();

    Page<Business> findByActiveTrue(Pageable pageable);

    boolean existsByTaxIdAndBusinessIdNotAndActiveTrue(String taxId, Integer businessId);

    @Query("SELECT b FROM Business b JOIN b.currentLevel l WHERE b.active = true AND l.levelId = :levelId")
    List<Business> findByLoyaltyLevel(@Param("levelId") Integer levelId);

    Optional<Business> findByTaxIdAndActiveTrue(String taxId);

    Optional<Business> findByUserUserId(Integer userId);

    @Query("SELECT b FROM Business b WHERE b.active = true ORDER BY b.businessName")
    Page<Business> findAllActive(Pageable pageable);

    @Query("SELECT b FROM Business b WHERE b.active = true AND " +
            "(LOWER(b.businessName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(b.legalName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(b.taxId) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Business> findActiveBySearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT b FROM Business b WHERE b.currentLevel.levelId = :levelId")
    List<Business> findByCurrentLevelId(@Param("levelId") Integer levelId);

    boolean existsByTaxIdAndActiveTrue(String taxId);

    boolean existsByBusinessEmailAndActiveTrue(String businessEmail);

    // Count businesses by loyalty level
    @Query("SELECT COUNT(b) FROM Business b WHERE b.currentLevel.levelId = :levelId")
    long countByCurrentLevelLevelId(@Param("levelId") Integer levelId);
}
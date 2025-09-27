package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * IMPORTANT: This is the complete ContractJpaRepository interface
 * Add these methods to your existing ContractJpaRepository
 */
@Repository
public interface ContractJpaRepository extends JpaRepository<Contract, Integer>, JpaSpecificationExecutor<Contract> {

        @Query("SELECT c FROM Contract c WHERE c.user.userId = :userId " +
                        "AND c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        Optional<Contract> findActiveContractByUserId(@Param("userId") Integer userId);

        @Query("SELECT c FROM Contract c WHERE c.user.userId = :userId ORDER BY c.createdAt DESC")
        List<Contract> findByUserId(@Param("userId") Integer userId);

        // Active contract counts
        @Query("SELECT COUNT(DISTINCT c.user.userId) FROM Contract c " +
                        "WHERE c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        long countCouriersWithActiveContracts();

        @Query("SELECT COUNT(c) FROM Contract c " +
                        "WHERE c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        long countActiveContracts();

        @Query("SELECT COUNT(c) FROM Contract c " +
                        "WHERE c.user.role.roleName = 'Repartidor' " +
                        "AND c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        long countActiveCourierContracts();

        // Contract validation queries
        @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contract c " +
                        "WHERE c.user.userId = :userId " +
                        "AND c.active = true " +
                        "AND :date BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        boolean hasActiveContractOnDate(@Param("userId") Integer userId, @Param("date") LocalDate date);

        @Query("SELECT c FROM Contract c WHERE c.user.userId = :userId " +
                        "AND c.active = true " +
                        "AND :date BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        Optional<Contract> findActiveContractByUserIdOnDate(@Param("userId") Integer userId,
                        @Param("date") LocalDate date);

        // Contracts by role
        @Query("SELECT c FROM Contract c WHERE c.user.role.roleName = :roleName " +
                        "AND c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        List<Contract> findActiveContractsByRole(@Param("roleName") String roleName);

        @Query("SELECT c FROM Contract c WHERE c.user.role.roleName = :roleName " +
                        "AND c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        Page<Contract> findActiveContractsByRole(@Param("roleName") String roleName, Pageable pageable);

        // Contract statistics
        @Query("SELECT COUNT(c) FROM Contract c WHERE c.user.role.roleName = 'Repartidor'")
        long countTotalCourierContracts();

        @Query("SELECT COUNT(c) FROM Contract c WHERE c.user.role.roleName = 'Coordinador'")
        long countTotalCoordinatorContracts();

        // Expiring contracts
        @Query("SELECT c FROM Contract c WHERE c.active = true " +
                        "AND c.endDate IS NOT NULL " +
                        "AND c.endDate BETWEEN :startDate AND :endDate " +
                        "ORDER BY c.endDate ASC")
        List<Contract> findExpiringContracts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT COUNT(c) FROM Contract c WHERE c.active = true " +
                        "AND c.endDate IS NOT NULL " +
                        "AND c.endDate BETWEEN CURRENT_DATE AND :endDate")
        long countExpiringContractsWithinDays(@Param("endDate") LocalDate endDate);

        // Contract by type
        @Query("SELECT c FROM Contract c WHERE c.contractType.contractTypeId = :typeId " +
                        "AND c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        List<Contract> findActiveContractsByType(@Param("typeId") Integer typeId);

        @Query("SELECT COUNT(c) FROM Contract c WHERE c.contractType.contractTypeId = :typeId " +
                        "AND c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        long countActiveContractsByType(@Param("typeId") Integer typeId);

        // Commission-related queries
        @Query("SELECT AVG(c.commissionPercentage) FROM Contract c " +
                        "WHERE c.user.role.roleName = 'Repartidor' " +
                        "AND c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        Double getAverageCommissionPercentage();

        @Query("SELECT MIN(c.commissionPercentage) FROM Contract c " +
                        "WHERE c.user.role.roleName = 'Repartidor' " +
                        "AND c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        Double getMinCommissionPercentage();

        @Query("SELECT MAX(c.commissionPercentage) FROM Contract c " +
                        "WHERE c.user.role.roleName = 'Repartidor' " +
                        "AND c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        Double getMaxCommissionPercentage();

        // Contracts by admin
        @Query("SELECT c FROM Contract c WHERE c.admin.userId = :adminId ORDER BY c.createdAt DESC")
        Page<Contract> findByAdminId(@Param("adminId") Integer adminId, Pageable pageable);

        @Query("SELECT COUNT(c) FROM Contract c WHERE c.admin.userId = :adminId")
        long countByAdminId(@Param("adminId") Integer adminId);

        // Recent contracts
        @Query("SELECT c FROM Contract c WHERE c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31') " +
                        "ORDER BY c.createdAt DESC")
        Page<Contract> findRecentActiveContracts(Pageable pageable);

        // Users without active contracts
        @Query("SELECT u FROM User u WHERE u.role.roleName = 'Repartidor' " +
                        "AND u.active = true " +
                        "AND u.userId NOT IN (SELECT c.user.userId FROM Contract c " +
                        "WHERE c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31'))")
        List<com.ayd.sie.shared.domain.entities.User> findCouriersWithoutActiveContract();

        @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = 'Repartidor' " +
                        "AND u.active = true " +
                        "AND u.userId NOT IN (SELECT c.user.userId FROM Contract c " +
                        "WHERE c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31'))")
        long countCouriersWithoutActiveContract();

        // Search contracts
        @Query("SELECT c FROM Contract c WHERE " +
                        "LOWER(c.user.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(c.user.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(c.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(c.contractType.typeName) LIKE LOWER(CONCAT('%', :search, '%'))")
        Page<Contract> searchContracts(@Param("search") String search, Pageable pageable);

        // Additional methods for DeleteEmployeeUseCase
        @Query("SELECT c FROM Contract c WHERE c.user.userId = :userId ORDER BY c.createdAt DESC")
        List<Contract> findByUserUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);

        // Additional methods for DeleteContractTypeUseCase
        @Query("SELECT COUNT(c) FROM Contract c WHERE c.contractType.contractTypeId = :contractTypeId")
        long countAllByContractTypeId(@Param("contractTypeId") Integer contractTypeId);

        @Query("SELECT COUNT(c) FROM Contract c WHERE c.contractType.contractTypeId = :contractTypeId AND c.active = true")
        long countActiveByContractTypeId(@Param("contractTypeId") Integer contractTypeId);

        @Query("SELECT COUNT(c) FROM Contract c WHERE c.contractType.contractTypeId = :contractTypeId " +
                        "AND c.active = true AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        long countCurrentlyValidByContractTypeId(@Param("contractTypeId") Integer contractTypeId);
}
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

@Repository
public interface ContractJpaRepository extends JpaRepository<Contract, Integer>, JpaSpecificationExecutor<Contract> {

        @Query("SELECT c FROM Contract c WHERE c.user.userId = :userId " +
                        "AND c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        Optional<Contract> findActiveContractByUserId(@Param("userId") Integer userId);

        // Add the missing method for checking active contracts on a specific date
        @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contract c WHERE c.user.userId = :userId " +
                        "AND c.active = true " +
                        "AND :date BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        boolean hasActiveContractOnDate(@Param("userId") Integer userId, @Param("date") LocalDate date);

        @Query("SELECT c FROM Contract c WHERE c.user.userId = :userId ORDER BY c.createdAt DESC")
        List<Contract> findByUserId(@Param("userId") Integer userId);

        // Add this method for GetContractsUseCase - search contracts
        @Query("SELECT c FROM Contract c WHERE (" +
                        "LOWER(c.user.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(c.user.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(c.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(c.contractType.typeName) LIKE LOWER(CONCAT('%', :search, '%')))")
        Page<Contract> findBySearch(@Param("search") String search, Pageable pageable);

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
                        "WHERE c.user.role.roleId = :roleId " +
                        "AND c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        long countActiveContractsByRoleId(@Param("roleId") Integer roleId);

        @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleId = :roleId " +
                        "AND u.active = true " +
                        "AND u.userId NOT IN (SELECT c.user.userId FROM Contract c " +
                        "WHERE c.active = true " +
                        "AND CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31'))")
        long countCouriersWithoutActiveContract(@Param("roleId") Integer roleId);

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
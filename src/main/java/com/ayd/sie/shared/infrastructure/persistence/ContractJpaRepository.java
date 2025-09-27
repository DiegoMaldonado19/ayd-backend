package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractJpaRepository extends JpaRepository<Contract, Integer> {

        @Query("SELECT c FROM Contract c WHERE c.user.userId = :userId AND c.active = true AND " +
                        "CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        Optional<Contract> findActiveContractByUserId(@Param("userId") Integer userId);

        List<Contract> findByUserUserIdOrderByCreatedAtDesc(Integer userId);

        Page<Contract> findByActiveTrue(Pageable pageable);

        @Query("SELECT c FROM Contract c WHERE c.active = true AND " +
                        "CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        List<Contract> findAllCurrentlyActive();

        @Query("SELECT c FROM Contract c WHERE c.endDate IS NOT NULL AND c.endDate < :date AND c.active = true")
        List<Contract> findExpiredContracts(@Param("date") LocalDate date);

        @Query("SELECT c FROM Contract c JOIN c.user u WHERE c.active = true AND " +
                        "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
        Page<Contract> findActiveBySearch(@Param("search") String search, Pageable pageable);

        @Query("SELECT COUNT(c) > 0 FROM Contract c WHERE c.contractType.contractTypeId = :contractTypeId AND c.active = true AND "
                        +
                        "CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        boolean existsActiveContractsByType(@Param("contractTypeId") Integer contractTypeId);

        // Check ANY contract (active or inactive) with this contract type
        @Query("SELECT COUNT(c) > 0 FROM Contract c WHERE c.contractType.contractTypeId = :contractTypeId")
        boolean existsAnyContractByType(@Param("contractTypeId") Integer contractTypeId);

        // Count all contracts by type
        @Query("SELECT COUNT(c) FROM Contract c WHERE c.contractType.contractTypeId = :contractTypeId")
        long countAllByContractTypeId(@Param("contractTypeId") Integer contractTypeId);

        // Count active contracts by type
        @Query("SELECT COUNT(c) FROM Contract c WHERE c.contractType.contractTypeId = :contractTypeId AND c.active = true")
        long countActiveByContractTypeId(@Param("contractTypeId") Integer contractTypeId);

        // Count currently valid contracts by type
        @Query("SELECT COUNT(c) FROM Contract c WHERE c.contractType.contractTypeId = :contractTypeId AND c.active = true AND "
                        +
                        "CURRENT_DATE BETWEEN c.startDate AND COALESCE(c.endDate, '9999-12-31')")
        long countCurrentlyValidByContractTypeId(@Param("contractTypeId") Integer contractTypeId);
}
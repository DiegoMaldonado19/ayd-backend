package com.ayd.sie.shared.infrastructure.persistence;

import com.ayd.sie.shared.domain.entities.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractTypeJpaRepository extends JpaRepository<ContractType, Integer> {

    List<ContractType> findByActiveTrueOrderByTypeName();

    Optional<ContractType> findByTypeNameAndActiveTrue(String typeName);

    boolean existsByTypeName(String typeName);
}
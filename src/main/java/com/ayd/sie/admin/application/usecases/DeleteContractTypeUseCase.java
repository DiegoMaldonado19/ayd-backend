package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.ContractType;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.ContractJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.ContractTypeJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteContractTypeUseCase {

    private final ContractTypeJpaRepository contractTypeRepository;
    private final ContractJpaRepository contractRepository;

    @Transactional
    public void execute(Integer contractTypeId) {
        ContractType contractType = contractTypeRepository.findById(contractTypeId)
                .orElseThrow(() -> new InvalidCredentialsException("Contract type not found"));

        // Check if contract type has active contracts
        boolean hasActiveContracts = contractRepository.existsActiveContractsByType(contractTypeId);
        if (hasActiveContracts) {
            throw new InvalidCredentialsException("Cannot delete contract type with active contracts");
        }

        contractTypeRepository.delete(contractType);
        log.info("Contract type permanently deleted with ID: {}", contractTypeId);
    }
}
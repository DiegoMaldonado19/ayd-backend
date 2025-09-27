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
public class DeactivateContractTypeUseCase {

    private final ContractTypeJpaRepository contractTypeRepository;
    private final ContractJpaRepository contractRepository;

    @Transactional
    public void execute(Integer contractTypeId) {
        ContractType contractType = contractTypeRepository.findById(contractTypeId)
                .orElseThrow(() -> new InvalidCredentialsException("Contract type not found"));

        if (!contractType.getActive()) {
            throw new InvalidCredentialsException("Contract type is already inactive");
        }

        boolean hasActiveContracts = contractRepository.existsActiveContractsByType(contractTypeId);
        if (hasActiveContracts) {
            throw new InvalidCredentialsException("Cannot deactivate contract type with active contracts");
        }

        contractType.setActive(false);
        contractTypeRepository.save(contractType);

        log.info("Deactivated contract type: {}", contractType.getTypeName());
    }
}
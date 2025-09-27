package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.ContractType;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.ContractTypeJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivateContractTypeUseCase {

    private final ContractTypeJpaRepository contractTypeRepository;

    @Transactional
    public void execute(Integer contractTypeId, boolean active) {
        ContractType contractType = contractTypeRepository.findById(contractTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract type not found"));

        contractType.setActive(active);
        contractTypeRepository.save(contractType);

        log.info("Contract type {} status changed to: {}", contractTypeId, active ? "ACTIVE" : "INACTIVE");
    }
}
package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.Contract;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.ContractJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteContractUseCase {

    private final ContractJpaRepository contractRepository;

    @Transactional
    public void execute(Integer contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new InvalidCredentialsException("Contract not found"));

        contractRepository.delete(contract);
        log.info("Contract permanently deleted with ID: {}", contractId);
    }
}
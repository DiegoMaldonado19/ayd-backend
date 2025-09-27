package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.Contract;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.ContractJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TerminateContractUseCase {

    private final ContractJpaRepository contractRepository;

    @Transactional
    public void execute(Integer contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new InvalidCredentialsException("Contract not found"));

        if (!Boolean.TRUE.equals(contract.getActive())) {
            throw new InvalidCredentialsException("Contract is already terminated");
        }

        if (!contract.isCurrentlyActive()) {
            throw new InvalidCredentialsException("Contract is not currently active");
        }

        contract.setActive(false);
        if (contract.getEndDate() == null || contract.getEndDate().isAfter(LocalDate.now())) {
            contract.setEndDate(LocalDate.now());
        }

        contractRepository.save(contract);

        log.info("Contract terminated successfully with ID: {}", contractId);
    }
}
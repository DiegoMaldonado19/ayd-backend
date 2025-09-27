package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.Contract;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        if (!Boolean.TRUE.equals(contract.getActive())) {
            throw new ResourceNotFoundException("Contract is already terminated");
        }

        LocalDate now = LocalDate.now();

        // Validate contract is within active period
        if (now.isBefore(contract.getStartDate())) {
            throw new ResourceNotFoundException("Contract has not started yet");
        }

        if (contract.getEndDate() != null && now.isAfter(contract.getEndDate())) {
            throw new ResourceNotFoundException("Contract has already expired");
        }

        contract.setActive(false);
        if (contract.getEndDate() == null || contract.getEndDate().isAfter(now)) {
            contract.setEndDate(now);
        }

        contractRepository.save(contract);

        log.info("Contract terminated successfully with ID: {}", contractId);
    }
}
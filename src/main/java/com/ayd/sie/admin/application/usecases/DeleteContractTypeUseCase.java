package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.ContractType;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.domain.exceptions.ResourceHasDependenciesException;
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
                // Find the contract type
                ContractType contractType = contractTypeRepository.findById(contractTypeId)
                                .orElseThrow(() -> new ResourceNotFoundException("Contract type not found"));

                // Count all contracts with this type (active and inactive)
                long totalContracts = contractRepository.countAllByContractTypeId(contractTypeId);
                long activeContracts = contractRepository.countActiveByContractTypeId(contractTypeId);
                long currentlyValidContracts = contractRepository.countCurrentlyValidByContractTypeId(contractTypeId);

                // Log the counts for debugging
                log.info("Contract type {} has {} total contracts ({} active, {} currently valid)",
                                contractTypeId, totalContracts, activeContracts, currentlyValidContracts);

                // Check if ANY contract exists with this type
                if (totalContracts > 0) {
                        String errorMessage = String.format(
                                        "Cannot delete contract type '%s' (ID: %d). It has %d contract(s) associated (%d active, %d currently valid). "
                                                        +
                                                        "Please delete or reassign all contracts before deleting this contract type.",
                                        contractType.getTypeName(), contractTypeId, totalContracts, activeContracts,
                                        currentlyValidContracts);
                        log.error(errorMessage);
                        throw new ResourceHasDependenciesException(errorMessage);
                }

                // Safe to delete - no contracts reference this type
                contractTypeRepository.delete(contractType);
                log.info("Contract type '{}' permanently deleted with ID: {}", contractType.getTypeName(),
                                contractTypeId);
        }
}
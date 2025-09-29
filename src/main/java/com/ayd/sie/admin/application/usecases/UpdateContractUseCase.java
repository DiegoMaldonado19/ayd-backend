package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.ContractDto;
import com.ayd.sie.admin.application.dto.UpdateContractRequestDto;
import com.ayd.sie.shared.domain.entities.Contract;
import com.ayd.sie.shared.domain.entities.ContractType;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.ContractJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.ContractTypeJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateContractUseCase {

    private final ContractJpaRepository contractRepository;
    private final ContractTypeJpaRepository contractTypeRepository;

    @Transactional
    public ContractDto execute(Integer contractId, UpdateContractRequestDto request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        // Update contract type if provided
        if (request.getContractTypeId() != null) {
            ContractType contractType = contractTypeRepository.findById(request.getContractTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contract type not found"));

            if (!Boolean.TRUE.equals(contractType.getActive())) {
                throw new ResourceNotFoundException("Cannot assign inactive contract type");
            }

            contract.setContractType(contractType);
        }

        // Update base salary if provided
        if (request.getBaseSalary() != null) {
            contract.setBaseSalary(request.getBaseSalary());
        }

        // Update commission percentage if provided
        if (request.getCommissionPercentage() != null) {
            contract.setCommissionPercentage(request.getCommissionPercentage());
        }

        // Update start date if provided
        if (request.getStartDate() != null) {
            contract.setStartDate(request.getStartDate());
        }

        // Update end date if provided
        if (request.getEndDate() != null) {
            contract.setEndDate(request.getEndDate());
        }

        // Validate that end date is not before start date
        if (contract.getEndDate() != null && contract.getEndDate().isBefore(contract.getStartDate())) {
            throw new ResourceNotFoundException("End date cannot be before start date");
        }

        // Update observations if provided
        if (request.getObservations() != null) {
            contract.setObservations(request.getObservations());
        }

        // Update active status if provided
        if (request.getActive() != null) {
            contract.setActive(request.getActive());
        }

        Contract savedContract = contractRepository.save(contract);

        log.info("Contract updated successfully for contractId: {} by user", contractId);

        return mapToDto(savedContract);
    }

    private ContractDto mapToDto(Contract contract) {
        return ContractDto.builder()
                .contractId(contract.getContractId())
                .userId(contract.getUser().getUserId())
                .userFullName(contract.getUser().getFullName())
                .userEmail(contract.getUser().getEmail())
                .adminId(contract.getAdmin().getUserId())
                .adminFullName(contract.getAdmin().getFullName())
                .contractTypeId(contract.getContractType().getContractTypeId())
                .contractTypeName(contract.getContractType().getTypeName())
                .baseSalary(contract.getBaseSalary())
                .commissionPercentage(contract.getCommissionPercentage())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .active(contract.getActive())
                .observations(contract.getObservations())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }
}
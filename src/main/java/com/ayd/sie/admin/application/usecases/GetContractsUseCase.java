package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.ContractDto;
import com.ayd.sie.shared.domain.entities.Contract;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.ContractJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetContractsUseCase {

    private final ContractJpaRepository contractRepository;

    @Transactional(readOnly = true)
    public Page<ContractDto> execute(String search, Pageable pageable) {
        Page<Contract> contracts;

        if (search != null && !search.trim().isEmpty()) {
            contracts = contractRepository.findBySearch(search.trim(), pageable);
        } else {
            contracts = contractRepository.findAll(pageable);
        }

        return contracts.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public ContractDto findById(Integer contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + contractId));
        return mapToDto(contract);
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
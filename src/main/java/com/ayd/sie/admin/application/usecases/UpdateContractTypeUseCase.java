package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.UpdateContractTypeRequestDto;
import com.ayd.sie.admin.application.dto.ContractTypeDto;
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
public class UpdateContractTypeUseCase {

    private final ContractTypeJpaRepository contractTypeRepository;

    @Transactional
    public ContractTypeDto execute(Integer contractTypeId, UpdateContractTypeRequestDto request) {
        ContractType contractType = contractTypeRepository.findById(contractTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract type not found"));

        if (!contractType.getActive()) {
            throw new ResourceNotFoundException("Cannot update inactive contract type");
        }

        if (!contractType.getTypeName().equals(request.getType_name()) &&
                contractTypeRepository.existsByTypeName(request.getType_name())) {
            throw new ResourceNotFoundException("Contract type name already exists");
        }

        contractType.setTypeName(request.getType_name());
        contractType.setDescription(request.getDescription());

        // Update active status if provided
        if (request.getActive() != null) {
            contractType.setActive(request.getActive());
        }

        ContractType savedContractType = contractTypeRepository.save(contractType);
        log.info("Updated contract type: {}", savedContractType.getTypeName());

        return mapToDto(savedContractType);
    }

    private ContractTypeDto mapToDto(ContractType contractType) {
        return ContractTypeDto.builder()
                .contract_type_id(contractType.getContractTypeId())
                .type_name(contractType.getTypeName())
                .description(contractType.getDescription())
                .active(contractType.getActive())
                .created_at(contractType.getCreatedAt())
                .build();
    }
}
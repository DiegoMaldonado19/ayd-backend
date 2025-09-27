package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.CreateContractTypeRequestDto;
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
public class CreateContractTypeUseCase {

    private final ContractTypeJpaRepository contractTypeRepository;

    @Transactional
    public ContractTypeDto execute(CreateContractTypeRequestDto request) {
        if (contractTypeRepository.existsByTypeName(request.getType_name())) {
            throw new ResourceNotFoundException("Contract type name already exists");
        }

        ContractType contractType = ContractType.builder()
                .typeName(request.getType_name())
                .description(request.getDescription())
                .active(true)
                .build();

        ContractType savedContractType = contractTypeRepository.save(contractType);
        log.info("Created new contract type: {}", savedContractType.getTypeName());

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
package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.ContractType;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.ContractTypeJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetContractTypesUseCase {

    private final ContractTypeJpaRepository contractTypeRepository;

    @Transactional(readOnly = true)
    public List<ContractType> execute() {
        return contractTypeRepository.findByActiveTrueOrderByTypeName();
    }

    @Transactional(readOnly = true)
    public ContractType findById(Integer contractTypeId) {
        return contractTypeRepository.findById(contractTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract type not found with id: " + contractTypeId));
    }
}
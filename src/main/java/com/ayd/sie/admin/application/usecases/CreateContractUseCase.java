package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.ContractDto;
import com.ayd.sie.admin.application.dto.CreateContractRequestDto;
import com.ayd.sie.shared.domain.entities.Contract;
import com.ayd.sie.shared.domain.entities.ContractType;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.ContractJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.ContractTypeJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateContractUseCase {

    private final ContractJpaRepository contractRepository;
    private final ContractTypeJpaRepository contractTypeRepository;
    private final UserJpaRepository userRepository;

    @Transactional
    public ContractDto execute(CreateContractRequestDto request, Integer adminId) {
        User employee = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new InvalidCredentialsException("Employee not found"));

        if (!employee.getRole().getRoleName().equals("Repartidor")) {
            throw new InvalidCredentialsException("Contracts can only be created for couriers");
        }

        if (!Boolean.TRUE.equals(employee.getActive())) {
            throw new InvalidCredentialsException("Cannot create contract for inactive employee");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new InvalidCredentialsException("Admin not found"));

        ContractType contractType = contractTypeRepository.findById(request.getContractTypeId())
                .orElseThrow(() -> new InvalidCredentialsException("Contract type not found"));

        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidCredentialsException("End date cannot be before start date");
        }

        // Check if there's already an active contract for this employee
        contractRepository.findActiveContractByUserId(request.getUserId())
                .ifPresent(existingContract -> {
                    throw new InvalidCredentialsException("Employee already has an active contract");
                });

        Contract contract = Contract.builder()
                .user(employee)
                .admin(admin)
                .contractType(contractType)
                .baseSalary(request.getBaseSalary())
                .commissionPercentage(request.getCommissionPercentage())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .observations(request.getObservations())
                .active(true)
                .build();

        Contract savedContract = contractRepository.save(contract);

        log.info("Contract created successfully for employee: {} by admin: {}",
                employee.getEmail(), admin.getEmail());

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
package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.BranchDto;
import com.ayd.sie.admin.application.dto.CreateBranchRequestDto;
import com.ayd.sie.shared.domain.entities.Branch;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.BranchJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateBranchUseCase {

    private final BranchJpaRepository branchRepository;

    @Transactional
    public BranchDto execute(CreateBranchRequestDto request) {
        if (branchRepository.existsByBranchCodeAndActiveTrue(request.getBranchCode())) {
            throw new InvalidCredentialsException("Branch code already exists");
        }

        Branch branch = Branch.builder()
                .branchCode(request.getBranchCode())
                .branchName(request.getBranchName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .city(request.getCity())
                .state(request.getState())
                .active(true)
                .build();

        Branch savedBranch = branchRepository.save(branch);

        log.info("Branch created successfully with code: {}", savedBranch.getBranchCode());

        return mapToDto(savedBranch);
    }

    private BranchDto mapToDto(Branch branch) {
        return BranchDto.builder()
                .branchId(branch.getBranchId())
                .branchCode(branch.getBranchCode())
                .branchName(branch.getBranchName())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .email(branch.getEmail())
                .city(branch.getCity())
                .state(branch.getState())
                .active(branch.getActive())
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }
}
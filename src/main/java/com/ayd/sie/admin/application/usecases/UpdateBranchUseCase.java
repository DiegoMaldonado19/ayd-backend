package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.BranchDto;
import com.ayd.sie.admin.application.dto.UpdateBranchRequestDto;
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
public class UpdateBranchUseCase {

    private final BranchJpaRepository branchRepository;

    @Transactional
    public BranchDto execute(Integer branchId, UpdateBranchRequestDto request) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new InvalidCredentialsException("Branch not found"));

        if (!Boolean.TRUE.equals(branch.getActive())) {
            throw new InvalidCredentialsException("Cannot update inactive branch");
        }

        branch.setBranchName(request.getBranchName());
        branch.setAddress(request.getAddress());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setCity(request.getCity());
        branch.setState(request.getState());

        Branch savedBranch = branchRepository.save(branch);

        log.info("Branch updated successfully with ID: {}", branchId);

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
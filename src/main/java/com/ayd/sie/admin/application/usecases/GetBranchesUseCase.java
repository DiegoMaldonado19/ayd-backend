package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.BranchDto;
import com.ayd.sie.shared.domain.entities.Branch;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.BranchJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetBranchesUseCase {

    private final BranchJpaRepository branchRepository;

    @Transactional(readOnly = true)
    public Page<BranchDto> execute(String search, Pageable pageable) {
        Page<Branch> branches;

        if (search != null && !search.trim().isEmpty()) {
            branches = branchRepository.findBySearch(search.trim(), pageable);
        } else {
            branches = branchRepository.findAll(pageable);
        }

        return branches.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public BranchDto findById(Integer branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));
        return mapToDto(branch);
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

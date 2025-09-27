package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.Branch;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.BranchJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivateBranchUseCase {

    private final BranchJpaRepository branchRepository;

    @Transactional
    public void execute(Integer branchId, boolean active) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        branch.setActive(active);
        branchRepository.save(branch);

        log.info("Branch {} status changed to: {}", branchId, active ? "ACTIVE" : "INACTIVE");
    }
}
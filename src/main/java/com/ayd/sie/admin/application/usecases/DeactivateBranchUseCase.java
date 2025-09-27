package com.ayd.sie.admin.application.usecases;

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
public class DeactivateBranchUseCase {

    private final BranchJpaRepository branchRepository;

    @Transactional
    public void execute(Integer branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new InvalidCredentialsException("Branch not found"));

        if (!Boolean.TRUE.equals(branch.getActive())) {
            throw new InvalidCredentialsException("Branch is already inactive");
        }

        branch.setActive(false);
        branchRepository.save(branch);

        log.info("Branch deactivated successfully with ID: {}", branchId);
    }
}
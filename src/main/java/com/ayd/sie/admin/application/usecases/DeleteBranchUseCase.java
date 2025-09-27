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
public class DeleteBranchUseCase {

    private final BranchJpaRepository branchRepository;

    @Transactional
    public void execute(Integer branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new InvalidCredentialsException("Branch not found"));

        // Note: Branch deletion validation would need tracking guides entity
        // For now, we'll allow deletion and rely on database constraints

        branchRepository.delete(branch);
        log.info("Branch permanently deleted with ID: {}", branchId);
    }
}
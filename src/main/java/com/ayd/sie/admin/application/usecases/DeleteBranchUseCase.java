package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.Branch;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.BranchJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteBranchUseCase {

    private final BranchJpaRepository branchRepository;
    private final TrackingGuideJpaRepository trackingGuideRepository;

    @Transactional
    public void execute(Integer branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        validateBranchCanBeDeleted(branchId, branch.getBranchCode());

        branchRepository.delete(branch);
        log.info("Branch permanently deleted with ID: {} and code: {}", branchId, branch.getBranchCode());
    }

    private void validateBranchCanBeDeleted(Integer branchId, String branchCode) {
        long totalGuides = trackingGuideRepository.countByOriginBranchId(branchId);
        if (totalGuides > 0) {
            long activeGuides = trackingGuideRepository.countActiveByOriginBranchId(branchId);
            throw new ResourceNotFoundException(
                    String.format(
                            "Cannot delete branch '%s'. It has %d tracking guides associated (%d active, %d completed/cancelled).",
                            branchCode, totalGuides, activeGuides, totalGuides - activeGuides));
        }
    }
}
package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.UserReferencesDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.domain.exceptions.ResourceHasDependenciesException;
import com.ayd.sie.shared.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteEmployeeUseCase {

    private final UserJpaRepository userRepository;
    private final ContractJpaRepository contractRepository;
    private final AuditLogJpaRepository auditLogRepository;
    private final CheckUserReferencesUseCase checkUserReferencesUseCase;

    @Transactional
    public void execute(Integer userId) {
        // Find the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        log.info("Attempting to delete user: {} ({})", user.getFullName(), user.getEmail());

        // Check all references in the system
        UserReferencesDto references = checkUserReferencesUseCase.execute(userId);

        // Validate based on role
        validateUserCanBeDeleted(user, references);

        // Set user_id to NULL in audit_log
        if (references.getAuditLogEntries() > 0) {
            log.info("Setting user_id to NULL for {} audit log entries", references.getAuditLogEntries());
            int updatedRows = auditLogRepository.setUserToNullByUserId(userId);
            log.info("Updated {} audit log entries", updatedRows);
        }

        // Delete all contracts associated with this user first
        var userContracts = contractRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
        if (!userContracts.isEmpty()) {
            log.info("Deleting {} contracts for user {}", userContracts.size(), userId);
            contractRepository.deleteAll(userContracts);
        }

        // Now delete the user
        userRepository.delete(user);
        log.info("Employee permanently deleted with ID: {} - {}", userId, user.getFullName());
    }

    private void validateUserCanBeDeleted(User user, UserReferencesDto references) {
        String roleName = user.getRole().getRoleName();
        log.info("Validating deletion for user with role: {}", roleName);

        // Build error message based on role and references
        StringBuilder errorMessage = new StringBuilder();
        boolean cannotDelete = false;

        switch (roleName) {
            case "Repartidor":
                if (references.getActiveGuidesAsCourier() > 0) {
                    errorMessage.append(String.format(
                            "Cannot delete courier '%s'. Has %d active tracking guides assigned. ",
                            user.getFullName(), references.getActiveGuidesAsCourier()));
                    cannotDelete = true;
                }
                if (references.getPendingSettlements() > 0) {
                    errorMessage.append(String.format(
                            "Has %d pending settlements that must be paid first. ",
                            references.getPendingSettlements()));
                    cannotDelete = true;
                }
                break;

            case "Coordinador":
                if (references.getActiveGuidesAsCoordinator() > 0) {
                    errorMessage.append(String.format(
                            "Cannot delete coordinator '%s'. Has %d active tracking guides under coordination. ",
                            user.getFullName(), references.getActiveGuidesAsCoordinator()));
                    cannotDelete = true;
                }
                break;

            case "Comercio Afiliado":
                if (references.isHasBusiness()) {
                    errorMessage.append(String.format(
                            "Cannot delete business user '%s'. Associated business '%s' must be deleted first. ",
                            user.getFullName(), references.getBusinessName()));
                    cannotDelete = true;
                }
                break;

            case "Administrador":
                // Admins can be deleted if they don't have critical active operations
                if (references.getActiveGuidesAsCoordinator() > 0) {
                    errorMessage.append(String.format(
                            "Cannot delete administrator '%s'. Has %d active tracking guides under coordination. ",
                            user.getFullName(), references.getActiveGuidesAsCoordinator()));
                    cannotDelete = true;
                }
                break;
        }

        // Check for historical data that prevents deletion
        if (!cannotDelete && references.isHasReferences()) {
            // Log historical references as warning but allow deletion
            log.warn("User {} has historical references: {} total guides as courier, {} as coordinator, " +
                    "{} state history entries, {} incidents reported, {} incidents resolved, " +
                    "{} cancellations, {} settlements",
                    user.getFullName(),
                    references.getTrackingGuidesAsCourier(),
                    references.getTrackingGuidesAsCoordinator(),
                    references.getStateHistoryEntries(),
                    references.getReportedIncidents(),
                    references.getResolvedIncidents(),
                    references.getCancellations(),
                    references.getCourierSettlements());
        }

        if (cannotDelete) {
            errorMessage.append("Please resolve these dependencies before attempting to delete the user.");
            throw new ResourceHasDependenciesException(errorMessage.toString(), references);
        }
    }
}
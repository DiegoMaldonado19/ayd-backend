package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.Role;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.RoleJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteRoleUseCase {

    private final RoleJpaRepository roleRepository;
    private final UserJpaRepository userRepository;

    @Transactional
    public void execute(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new InvalidCredentialsException("Role not found"));

        // Check if role has active users
        boolean hasActiveUsers = !userRepository.findActiveUsersByRole(roleId).isEmpty();
        if (hasActiveUsers) {
            throw new InvalidCredentialsException("Cannot delete role with active users");
        }

        roleRepository.delete(role);
        log.info("Role permanently deleted with ID: {}", roleId);
    }
}
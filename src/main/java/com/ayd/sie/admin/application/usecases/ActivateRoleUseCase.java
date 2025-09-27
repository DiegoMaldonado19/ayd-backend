package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.Role;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivateRoleUseCase {

    private final RoleJpaRepository roleRepository;

    @Transactional
    public void execute(Integer roleId, boolean active) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        role.setActive(active);
        roleRepository.save(role);

        log.info("Role {} status changed to: {}", roleId, active ? "ACTIVE" : "INACTIVE");
    }
}
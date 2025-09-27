package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.UpdateRoleRequestDto;
import com.ayd.sie.admin.application.dto.RoleDto;
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
public class UpdateRoleUseCase {

    private final RoleJpaRepository roleRepository;

    @Transactional
    public RoleDto execute(Integer roleId, UpdateRoleRequestDto request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        if (!role.getActive()) {
            throw new ResourceNotFoundException("Cannot update inactive role");
        }

        if (!role.getRoleName().equals(request.getRole_name()) &&
                roleRepository.existsByRoleName(request.getRole_name())) {
            throw new ResourceNotFoundException("Role name already exists");
        }

        role.setRoleName(request.getRole_name());
        role.setDescription(request.getDescription());

        Role savedRole = roleRepository.save(role);
        log.info("Updated role: {}", savedRole.getRoleName());

        return mapToDto(savedRole);
    }

    private RoleDto mapToDto(Role role) {
        return RoleDto.builder()
                .role_id(role.getRoleId())
                .role_name(role.getRoleName())
                .description(role.getDescription())
                .active(role.getActive())
                .created_at(role.getCreatedAt())
                .updated_at(role.getUpdatedAt())
                .build();
    }
}
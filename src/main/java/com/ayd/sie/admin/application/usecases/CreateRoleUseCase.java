package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.CreateRoleRequestDto;
import com.ayd.sie.admin.application.dto.RoleDto;
import com.ayd.sie.shared.domain.entities.Role;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateRoleUseCase {

    private final RoleJpaRepository roleRepository;

    @Transactional
    public RoleDto execute(CreateRoleRequestDto request) {
        if (roleRepository.existsByRoleName(request.getRole_name())) {
            throw new InvalidCredentialsException("Role name already exists");
        }

        Role role = Role.builder()
                .roleName(request.getRole_name())
                .description(request.getDescription())
                .active(true)
                .build();

        Role savedRole = roleRepository.save(role);
        log.info("Created new role: {}", savedRole.getRoleName());

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
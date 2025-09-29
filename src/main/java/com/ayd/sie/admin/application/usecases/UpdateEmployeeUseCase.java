package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.EmployeeDto;
import com.ayd.sie.admin.application.dto.UpdateEmployeeRequestDto;
import com.ayd.sie.shared.domain.entities.Role;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.ContractJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.RoleJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateEmployeeUseCase {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final ContractJpaRepository contractRepository;

    @Transactional
    public EmployeeDto execute(Integer userId, UpdateEmployeeRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if email is being changed and if it already exists
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ResourceNotFoundException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        // Check if national ID is being changed and if it already exists
        if (request.getNationalId() != null && !request.getNationalId().equals(user.getNationalId())) {
            if (userRepository.existsByNationalId(request.getNationalId())) {
                throw new ResourceNotFoundException("National ID already exists");
            }
            user.setNationalId(request.getNationalId());
        }

        // Update role if provided
        if (request.getRoleId() != null && !request.getRoleId().equals(user.getRole().getRoleId())) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new AccessDeniedException("Invalid role"));

            if (!role.getRoleName().equals("Coordinador") && !role.getRoleName().equals("Repartidor")) {
                throw new ResourceNotFoundException("Can only assign Coordinator and Courier roles");
            }
            user.setRole(role);
        }

        // Update other fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        User savedUser = userRepository.save(user);

        log.info("Employee updated successfully with ID: {}", userId);

        return mapToDto(savedUser);
    }

    private EmployeeDto mapToDto(User user) {
        boolean hasActiveContract = contractRepository.findActiveContractByUserId(user.getUserId()).isPresent();

        return EmployeeDto.builder()
                .userId(user.getUserId())
                .roleId(user.getRole().getRoleId())
                .roleName(user.getRole().getRoleName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .nationalId(user.getNationalId())
                .active(user.getActive())
                .twoFactorEnabled(user.getTwoFactorEnabled())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .hasActiveContract(hasActiveContract)
                .build();
    }
}
package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.EmployeeDto;
import com.ayd.sie.admin.application.dto.EmployeeRegistrationRequestDto;
import com.ayd.sie.shared.domain.entities.Role;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.domain.services.NotificationService;
import com.ayd.sie.shared.infrastructure.persistence.ContractJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.RoleJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterEmployeeUseCase {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final ContractJpaRepository contractRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public EmployeeDto execute(EmployeeRegistrationRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidCredentialsException("Email already exists");
        }

        if (request.getNationalId() != null && userRepository.existsByNationalId(request.getNationalId())) {
            throw new InvalidCredentialsException("National ID already exists");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid role"));

        if (!role.getRoleName().equals("Coordinador") && !role.getRoleName().equals("Repartidor")) {
            throw new InvalidCredentialsException("Can only register Coordinators and Couriers");
        }

        String temporaryPassword = request.getTemporaryPassword() != null ? request.getTemporaryPassword()
                : generateTemporaryPassword();
        String hashedPassword = passwordEncoder.encode(temporaryPassword);

        User user = User.builder()
                .role(role)
                .email(request.getEmail())
                .passwordHash(hashedPassword)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .nationalId(request.getNationalId())
                .active(true)
                .twoFactorEnabled(false)
                .failedLoginAttempts(0)
                .build();

        User savedUser = userRepository.save(user);

        notificationService.sendWelcomeEmail(savedUser, temporaryPassword);

        log.info("Employee registered successfully with email: {}", request.getEmail());

        return mapToDto(savedUser);
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder(12);

        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }

        return password.toString();
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
package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.EmployeeDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.ContractJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetEmployeesUseCase {

    private final UserJpaRepository userRepository;
    private final ContractJpaRepository contractRepository;

    @Transactional(readOnly = true)
    public Page<EmployeeDto> execute(Integer roleId, String search, Pageable pageable) {
        List<User> allEmployees;

        if (roleId != null) {
            allEmployees = userRepository.findByRoleRoleIdAndActiveTrue(roleId);
        } else {
            // Get coordinators and couriers (roles 2 and 3)
            List<User> coordinators = userRepository.findByRoleRoleIdAndActiveTrue(2);
            List<User> couriers = userRepository.findByRoleRoleIdAndActiveTrue(3);
            allEmployees = coordinators;
            allEmployees.addAll(couriers);
        }

        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            allEmployees = allEmployees.stream()
                    .filter(user -> user.getFirstName().toLowerCase().contains(searchLower) ||
                            user.getLastName().toLowerCase().contains(searchLower) ||
                            user.getEmail().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        List<EmployeeDto> employeeDtos = allEmployees.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), employeeDtos.size());
        List<EmployeeDto> pageContent = employeeDtos.subList(start, end);

        return new PageImpl<>(pageContent, pageable, allEmployees.size());
    }

    @Transactional(readOnly = true)
    public EmployeeDto findById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + userId));
        return mapToDto(user);
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
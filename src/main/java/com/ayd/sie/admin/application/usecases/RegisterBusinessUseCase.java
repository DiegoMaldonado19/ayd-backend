package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.admin.application.dto.BusinessDto;
import com.ayd.sie.admin.application.dto.BusinessRegistrationRequestDto;
import com.ayd.sie.shared.domain.entities.Business;
import com.ayd.sie.shared.domain.entities.LoyaltyLevel;
import com.ayd.sie.shared.domain.entities.Role;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import com.ayd.sie.shared.domain.services.NotificationService;
import com.ayd.sie.shared.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterBusinessUseCase {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final BusinessJpaRepository businessRepository;
    private final LoyaltyLevelJpaRepository loyaltyLevelRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public BusinessDto execute(BusinessRegistrationRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceNotFoundException("Email already exists");
        }

        if (businessRepository.existsByTaxIdAndActiveTrue(request.getTaxId())) {
            throw new ResourceNotFoundException("Tax ID already exists");
        }

        if (request.getNationalId() != null && userRepository.existsByNationalId(request.getNationalId())) {
            throw new ResourceNotFoundException("National ID already exists");
        }

        Role businessRole = roleRepository.findByRoleNameAndActiveTrue("Comercio")
                .orElseThrow(() -> new AccessDeniedException("Business role not found"));

        LoyaltyLevel initialLevel = loyaltyLevelRepository.findById(request.getInitialLevelId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid loyalty level"));

        String temporaryPassword = generateTemporaryPassword();
        String hashedPassword = passwordEncoder.encode(temporaryPassword);

        User user = User.builder()
                .role(businessRole)
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

        Business business = Business.builder()
                .user(savedUser)
                .currentLevel(initialLevel)
                .taxId(request.getTaxId())
                .businessName(request.getBusinessName())
                .legalName(request.getLegalName())
                .taxAddress(request.getTaxAddress())
                .businessPhone(request.getBusinessPhone())
                .businessEmail(request.getBusinessEmail())
                .supportContact(request.getSupportContact())
                .affiliationDate(request.getAffiliationDate())
                .active(true)
                .build();

        Business savedBusiness = businessRepository.save(business);

        notificationService.sendWelcomeEmail(savedUser, temporaryPassword);

        log.info("Business registered successfully with tax ID: {}", request.getTaxId());

        return mapToDto(savedBusiness);
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder(12);

        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }

        return password.toString();
    }

    private BusinessDto mapToDto(Business business) {
        return BusinessDto.builder()
                .businessId(business.getBusinessId())
                .userId(business.getUser().getUserId())
                .userEmail(business.getUser().getEmail())
                .userFullName(business.getUser().getFullName())
                .currentLevelId(business.getCurrentLevel().getLevelId())
                .currentLevelName(business.getCurrentLevel().getLevelName())
                .taxId(business.getTaxId())
                .businessName(business.getBusinessName())
                .legalName(business.getLegalName())
                .taxAddress(business.getTaxAddress())
                .businessPhone(business.getBusinessPhone())
                .businessEmail(business.getBusinessEmail())
                .supportContact(business.getSupportContact())
                .active(business.getActive())
                .affiliationDate(business.getAffiliationDate())
                .createdAt(business.getCreatedAt())
                .updatedAt(business.getUpdatedAt())
                .build();
    }
}
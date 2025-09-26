package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.UserProfileDto;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserProfileUseCase {

    private final UserJpaRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileDto execute(String currentUserEmail) {
        User user = userRepository.findByEmailAndActiveTrue(currentUserEmail)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        return UserProfileDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole().getRoleName())
                .twoFactorEnabled(user.getTwoFactorEnabled())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
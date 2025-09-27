package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivateEmployeeUseCase {

    private final UserJpaRepository userRepository;

    @Transactional
    public void execute(Integer userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        user.setActive(active);
        userRepository.save(user);

        log.info("Employee {} with ID: {}", active ? "activated" : "deactivated", userId);
    }
}
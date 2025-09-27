package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.ContractJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteEmployeeUseCase {

    private final UserJpaRepository userRepository;
    private final ContractJpaRepository contractRepository;

    @Transactional
    public void execute(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("Employee not found"));

        // Delete all contracts associated with this user first (to avoid foreign key
        // constraint)
        var userContracts = contractRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
        if (!userContracts.isEmpty()) {
            log.info("Deleting {} contracts for user {}", userContracts.size(), userId);
            contractRepository.deleteAll(userContracts);
        }

        // Now delete the user
        userRepository.delete(user);
        log.info("Employee permanently deleted with ID: {}", userId);
    }
}
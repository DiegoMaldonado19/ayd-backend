package com.ayd.sie.admin.application.usecases;

import com.ayd.sie.shared.domain.entities.Role;
import com.ayd.sie.shared.infrastructure.persistence.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetRolesUseCase {

    private final RoleJpaRepository roleRepository;

    @Transactional(readOnly = true)
    public List<Role> execute() {
        return roleRepository.findByActiveTrue();
    }
}
package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.RevokeTokenRequestDto;
import com.ayd.sie.shared.domain.entities.RefreshToken;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.domain.exceptions.InvalidTokenException;
import com.ayd.sie.shared.infrastructure.persistence.RefreshTokenJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevokeTokenUseCase {

    private final UserJpaRepository userRepository;
    private final RefreshTokenJpaRepository refreshTokenRepository;

    @Transactional
    public void execute(RevokeTokenRequestDto request, String currentUserEmail) {
        User user = userRepository.findByEmailAndActiveTrue(currentUserEmail)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        RefreshToken token = refreshTokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new InvalidTokenException("Token not found"));

        if (!token.getUser().getUserId().equals(user.getUserId())) {
            throw new InvalidCredentialsException("Token does not belong to current user");
        }

        if (token.isRevoked()) {
            throw new InvalidTokenException("Token is already revoked");
        }

        token.revoke();
        refreshTokenRepository.save(token);

        log.info("Token revoked successfully for user: {} - Token ID: {}", 
                currentUserEmail, request.getTokenId());
    }
}
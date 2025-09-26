package com.ayd.sie.auth.application.usecases;

import com.ayd.sie.auth.application.dto.ListUserSessionsResponseDto;
import com.ayd.sie.auth.application.dto.UserSessionDto;
import com.ayd.sie.shared.domain.entities.RefreshToken;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.domain.exceptions.InvalidCredentialsException;
import com.ayd.sie.shared.infrastructure.persistence.RefreshTokenJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListUserSessionsUseCase {

    private final UserJpaRepository userRepository;
    private final RefreshTokenJpaRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public ListUserSessionsResponseDto execute(String currentUserEmail, String currentTokenHash) {
        User user = userRepository.findByEmailAndActiveTrue(currentUserEmail)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        List<RefreshToken> activeTokens = refreshTokenRepository.findActiveTokensByUser(user);

        List<UserSessionDto> sessions = activeTokens.stream()
                .map(token -> UserSessionDto.builder()
                        .tokenId(token.getTokenId())
                        .ipAddress(token.getIpAddress())
                        .userAgent(token.getUserAgent())
                        .createdAt(token.getCreatedAt())
                        .expiresAt(token.getExpiresAt())
                        .isCurrent(token.getTokenHash().equals(currentTokenHash))
                        .build())
                .collect(Collectors.toList());

        return ListUserSessionsResponseDto.builder()
                .activeSessions(sessions)
                .totalCount(sessions.size())
                .build();
    }
}
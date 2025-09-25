package com.ayd.sie.auth.application.services;

import com.ayd.sie.auth.application.dto.*;
import com.ayd.sie.auth.application.usecases.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final LoginUseCase loginUseCase;
    private final TwoFactorUseCase twoFactorUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    public LoginResponseDto login(LoginRequestDto request, String ipAddress, String userAgent) {
        return loginUseCase.execute(request, ipAddress, userAgent);
    }

    public LoginResponseDto verifyTwoFactor(TwoFactorRequestDto request, String ipAddress, String userAgent) {
        return twoFactorUseCase.execute(request, ipAddress, userAgent);
    }

    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto request, String ipAddress, String userAgent) {
        return refreshTokenUseCase.execute(request, ipAddress, userAgent);
    }

    public void logout(String email) {
        logoutUseCase.execute(email);
    }
}
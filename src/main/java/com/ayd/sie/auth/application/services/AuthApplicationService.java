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
    
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final ValidateResetTokenUseCase validateResetTokenUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    private final Enable2FAUseCase enable2FAUseCase;
    private final Disable2FAUseCase disable2FAUseCase;
    private final Resend2FACodeUseCase resend2FACodeUseCase;
    
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final ListUserSessionsUseCase listUserSessionsUseCase;
    private final ValidateTokenUseCase validateTokenUseCase;
    private final RevokeTokenUseCase revokeTokenUseCase;

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

    public void forgotPassword(ForgotPasswordRequestDto request) {
        forgotPasswordUseCase.execute(request);
    }

    public void resetPassword(ResetPasswordRequestDto request) {
        resetPasswordUseCase.execute(request);
    }

    public ValidateResetTokenResponseDto validateResetToken(ValidateResetTokenRequestDto request) {
        return validateResetTokenUseCase.execute(request);
    }

    public void changePassword(ChangePasswordRequestDto request, String currentUserEmail) {
        changePasswordUseCase.execute(request, currentUserEmail);
    }

    public Enable2FAResponseDto enable2FA(Enable2FARequestDto request, String currentUserEmail) {
        return enable2FAUseCase.execute(request, currentUserEmail);
    }

    public void disable2FA(Disable2FARequestDto request, String currentUserEmail) {
        disable2FAUseCase.execute(request, currentUserEmail);
    }

    public void resend2FACode(Resend2FACodeRequestDto request) {
        resend2FACodeUseCase.execute(request);
    }

    public UserProfileDto getUserProfile(String currentUserEmail) {
        return getUserProfileUseCase.execute(currentUserEmail);
    }

    public ListUserSessionsResponseDto listUserSessions(String currentUserEmail, String currentTokenHash) {
        return listUserSessionsUseCase.execute(currentUserEmail, currentTokenHash);
    }

    public ValidateTokenResponseDto validateToken(ValidateTokenRequestDto request) {
        return validateTokenUseCase.execute(request);
    }

    public void revokeToken(RevokeTokenRequestDto request, String currentUserEmail) {
        revokeTokenUseCase.execute(request, currentUserEmail);
    }
}
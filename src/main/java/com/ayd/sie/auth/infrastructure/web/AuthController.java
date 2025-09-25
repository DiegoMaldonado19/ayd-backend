package com.ayd.sie.auth.infrastructure.web;

import com.ayd.sie.auth.application.dto.*;
import com.ayd.sie.auth.application.services.AuthApplicationService;
import com.ayd.sie.shared.infrastructure.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email and password. Returns JWT tokens or requires 2FA verification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful or 2FA required", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials or account locked", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto loginRequest,
            HttpServletRequest request) {

        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        LoginResponseDto response = authApplicationService.login(loginRequest, ipAddress, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-2fa")
    @Operation(summary = "Verify two-factor authentication", description = "Complete login process by verifying the 2FA code sent to user's email or phone.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "2FA verification successful", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired verification code", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<LoginResponseDto> verifyTwoFactor(
            @Valid @RequestBody TwoFactorRequestDto twoFactorRequest,
            HttpServletRequest request) {

        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        LoginResponseDto response = authApplicationService.verifyTwoFactor(twoFactorRequest, ipAddress, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token", description = "Generate a new access token using a valid refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = RefreshTokenResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto refreshRequest,
            HttpServletRequest request) {

        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        RefreshTokenResponseDto response = authApplicationService.refreshToken(refreshRequest, ipAddress, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and revoke all refresh tokens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Object>> logout(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails != null) {
            authApplicationService.logout(userDetails.getUsername());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logout successful");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Send password reset email to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset email sent if user exists"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto request) {

        authApplicationService.forgotPassword(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "If the email exists, a password reset link has been sent");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using valid reset token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "400", description = "Invalid token or password validation failed"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired reset token")
    })
    public ResponseEntity<Map<String, Object>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto request) {

        authApplicationService.resetPassword(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password reset successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-reset-token")
    @Operation(summary = "Validate reset token", description = "Check if a password reset token is valid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validation result", content = @Content(schema = @Schema(implementation = ValidateResetTokenResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<ValidateResetTokenResponseDto> validateResetToken(
            @Valid @RequestBody ValidateResetTokenRequestDto request) {

        ValidateResetTokenResponseDto response = authApplicationService.validateResetToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password for authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password or validation failed"),
            @ApiResponse(responseCode = "401", description = "Authentication required or current password incorrect")
    })
    public ResponseEntity<Map<String, Object>> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        authApplicationService.changePassword(request, userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password changed successfully. All sessions have been logged out.");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/enable-2fa")
    @Operation(summary = "Enable 2FA", description = "Enable two-factor authentication for user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "2FA enabled successfully", content = @Content(schema = @Schema(implementation = Enable2FAResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid password or 2FA already enabled"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<Enable2FAResponseDto> enable2FA(
            @Valid @RequestBody Enable2FARequestDto request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        Enable2FAResponseDto response = authApplicationService.enable2FA(request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/disable-2fa")
    @Operation(summary = "Disable 2FA", description = "Disable two-factor authentication for user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "2FA disabled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password or verification code"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<Map<String, Object>> disable2FA(
            @Valid @RequestBody Disable2FARequestDto request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        authApplicationService.disable2FA(request, userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Two-factor authentication disabled successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-2fa-code")
    @Operation(summary = "Resend 2FA code", description = "Resend two-factor authentication code to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "2FA code sent if user has 2FA enabled"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> resend2FACode(
            @Valid @RequestBody Resend2FACodeRequestDto request) {

        authApplicationService.resend2FACode(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "If 2FA is enabled for this account, a new code has been sent");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // FASE 3 - ENDPOINTS OPCIONALES

    @GetMapping("/me")
    @Operation(summary = "Get user profile", description = "Get current user profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved", content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<UserProfileDto> getUserProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        UserProfileDto response = authApplicationService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions")
    @Operation(summary = "List user sessions", description = "Get list of active sessions for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessions retrieved", content = @Content(schema = @Schema(implementation = ListUserSessionsResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<ListUserSessionsResponseDto> listUserSessions(
            HttpServletRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        String currentTokenHash = extractTokenHash(request);
        ListUserSessionsResponseDto response = authApplicationService.listUserSessions(
                userDetails.getUsername(), currentTokenHash);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-token")
    @Operation(summary = "Validate token", description = "Validate if a JWT token is valid and active")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validation result", content = @Content(schema = @Schema(implementation = ValidateTokenResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<ValidateTokenResponseDto> validateToken(
            @Valid @RequestBody ValidateTokenRequestDto request) {

        ValidateTokenResponseDto response = authApplicationService.validateToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/revoke-token")
    @Operation(summary = "Revoke token", description = "Revoke a specific refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token revoked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid token ID"),
            @ApiResponse(responseCode = "401", description = "Authentication required or token doesn't belong to user")
    })
    public ResponseEntity<Map<String, Object>> revokeToken(
            @Valid @RequestBody RevokeTokenRequestDto request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        authApplicationService.revokeToken(request, userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Token revoked successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    private String extractTokenHash(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
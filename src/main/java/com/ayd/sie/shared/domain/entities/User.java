package com.ayd.sie.shared.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "national_id", unique = true, length = 20)
    private String nationalId;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "two_factor_enabled")
    @Builder.Default
    private Boolean twoFactorEnabled = false;

    @Column(name = "two_factor_code", length = 6)
    private String twoFactorCode;

    @Column(name = "two_factor_expiration")
    private LocalDateTime twoFactorExpiration;

    @Column(name = "password_reset_token", length = 8)
    private String passwordResetToken;

    @Column(name = "password_reset_expiration")
    private LocalDateTime passwordResetExpiration;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isAccountNonExpired() {
        return active != null && active;
    }

    public boolean isAccountNonLocked() {
        return lockedUntil == null || LocalDateTime.now().isAfter(lockedUntil);
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return active != null && active;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isTwoFactorRequired() {
        return twoFactorEnabled != null && twoFactorEnabled;
    }

    public boolean isTwoFactorCodeValid(String code) {
        return twoFactorCode != null &&
                twoFactorCode.equals(code) &&
                twoFactorExpiration != null &&
                LocalDateTime.now().isBefore(twoFactorExpiration);
    }

    public boolean isPasswordResetTokenValid(String token) {
        return passwordResetToken != null &&
                passwordResetToken.equals(token) &&
                passwordResetExpiration != null &&
                LocalDateTime.now().isBefore(passwordResetExpiration);
    }

    public void clearPasswordResetToken() {
        this.passwordResetToken = null;
        this.passwordResetExpiration = null;
    }
}
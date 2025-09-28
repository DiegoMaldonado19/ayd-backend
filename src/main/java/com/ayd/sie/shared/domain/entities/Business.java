package com.ayd.sie.shared.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "businesses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_id")
    private Integer businessId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_level_id")
    private LoyaltyLevel currentLevel;

    @Column(name = "tax_id", nullable = false, unique = true, length = 20)
    private String taxId;

    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName;

    @Column(name = "legal_name", nullable = false, length = 100)
    private String legalName;

    @Column(name = "tax_address", nullable = false, columnDefinition = "TEXT")
    private String taxAddress;

    @Column(name = "business_phone", length = 20)
    private String businessPhone;

    @Column(name = "business_email", length = 100)
    private String businessEmail;

    @Column(name = "support_contact", length = 100)
    private String supportContact;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "affiliation_date", nullable = false)
    private LocalDate affiliationDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public LoyaltyLevel getLoyaltyLevel() {
        return this.currentLevel;
    }

    public String getEmail() {
        return this.businessEmail != null ? this.businessEmail : (this.user != null ? this.user.getEmail() : null);
    }
}
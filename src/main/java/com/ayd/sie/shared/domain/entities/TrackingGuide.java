package com.ayd.sie.shared.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_guides")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TrackingGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id")
    private Integer guideId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_branch_id", nullable = false)
    private Branch originBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private User courier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinator_id")
    private User coordinator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_state_id", nullable = false)
    private TrackingState currentState;

    @Column(name = "guide_number", nullable = false, unique = true, length = 20)
    private String guideNumber;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "courier_commission", precision = 10, scale = 2)
    private BigDecimal courierCommission;

    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;

    @Column(name = "recipient_address", nullable = false, columnDefinition = "TEXT")
    private String recipientAddress;

    @Column(name = "recipient_city", nullable = false, length = 100)
    private String recipientCity;

    @Column(name = "recipient_state", nullable = false, length = 100)
    private String recipientState;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "assignment_accepted")
    @Builder.Default
    private Boolean assignmentAccepted = false;

    @Column(name = "assignment_accepted_at")
    private LocalDateTime assignmentAcceptedAt;

    @Column(name = "assignment_date")
    private LocalDateTime assignmentDate;

    @Column(name = "pickup_date")
    private LocalDateTime pickupDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "cancellation_date")
    private LocalDateTime cancellationDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method
    public User getAssignedCourier() {
        return this.courier;
    }
}
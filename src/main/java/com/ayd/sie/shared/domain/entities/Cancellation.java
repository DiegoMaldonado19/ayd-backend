package com.ayd.sie.shared.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cancellations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cancellation_id")
    private Integer cancellationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id", nullable = false, unique = true)
    private TrackingGuide guide;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelled_by_user_id", nullable = false)
    private User cancelledByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancellation_type_id", nullable = false)
    private CancellationType cancellationType;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "penalty_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal penaltyAmount = BigDecimal.ZERO;

    @Column(name = "courier_commission", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal courierCommission = BigDecimal.ZERO;

    @Column(name = "cancelled_at", nullable = false)
    private LocalDateTime cancelledAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "coordinator_notes", columnDefinition = "TEXT")
    private String coordinatorNotes;
}
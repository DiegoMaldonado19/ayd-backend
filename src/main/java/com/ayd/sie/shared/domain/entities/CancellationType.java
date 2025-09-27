package com.ayd.sie.shared.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "cancellation_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CancellationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cancellation_type_id")
    private Integer cancellationTypeId;

    @Column(name = "type_name", nullable = false, length = 50)
    private String typeName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
package com.sachin.appraisal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appraisal_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppraisalAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long appraisalId;

    @Column(nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private double finalScore;

    @Column(nullable = false)
    private LocalDateTime finalizedAt;

    @Column(nullable = false)
    private String finalizedBy;
}

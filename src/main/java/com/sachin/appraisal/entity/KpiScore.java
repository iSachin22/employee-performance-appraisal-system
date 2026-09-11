package com.sachin.appraisal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "kpi_scores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KpiScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appraisal_id", nullable = false)
    private Appraisal appraisal;

    @Column(nullable = false)
    private String kpiName;

    @Column(nullable = false)
    private double weight;

    private Integer selfRating;

    private Integer managerRating;
}

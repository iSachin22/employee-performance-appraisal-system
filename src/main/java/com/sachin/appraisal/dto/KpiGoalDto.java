package com.sachin.appraisal.dto;

import jakarta.validation.constraints.NotBlank;

public class KpiGoalDto {

    @NotBlank
    private String kpiName;

    private double weight;

    public String getKpiName() { return kpiName; }
    public void setKpiName(String kpiName) { this.kpiName = kpiName; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}

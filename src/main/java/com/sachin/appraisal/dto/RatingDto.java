package com.sachin.appraisal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class RatingDto {

    private Long kpiScoreId;

    @Min(1)
    @Max(5)
    private int rating;

    public Long getKpiScoreId() { return kpiScoreId; }
    public void setKpiScoreId(Long kpiScoreId) { this.kpiScoreId = kpiScoreId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}

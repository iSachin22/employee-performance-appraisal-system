package com.sachin.appraisal.service;

import com.sachin.appraisal.dto.KpiGoalDto;
import com.sachin.appraisal.dto.RatingDto;
import com.sachin.appraisal.entity.*;
import com.sachin.appraisal.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppraisalService {

    private final AppraisalRepository appraisalRepository;
    private final EmployeeRepository employeeRepository;
    private final KpiScoreRepository kpiScoreRepository;
    private final AppraisalAuditLogRepository auditLogRepository;

    public AppraisalService(AppraisalRepository appraisalRepository,
                             EmployeeRepository employeeRepository,
                             KpiScoreRepository kpiScoreRepository,
                             AppraisalAuditLogRepository auditLogRepository) {
        this.appraisalRepository = appraisalRepository;
        this.employeeRepository = employeeRepository;
        this.kpiScoreRepository = kpiScoreRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public Appraisal startAppraisal(Long employeeId, String reviewCycle, List<KpiGoalDto> goals) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        double totalWeight = goals.stream().mapToDouble(KpiGoalDto::getWeight).sum();
        if (Math.abs(totalWeight - 1.0) > 0.001) {
            throw new IllegalArgumentException("KPI weights must sum to 1.0 (got " + totalWeight + ")");
        }

        Appraisal appraisal = new Appraisal();
        appraisal.setEmployee(employee);
        appraisal.setReviewCycle(reviewCycle);
        appraisal.setStatus(AppraisalStatus.GOAL_SETTING);
        appraisal = appraisalRepository.save(appraisal);

        for (KpiGoalDto goal : goals) {
            KpiScore score = new KpiScore();
            score.setAppraisal(appraisal);
            score.setKpiName(goal.getKpiName());
            score.setWeight(goal.getWeight());
            kpiScoreRepository.save(score);
        }
        return appraisal;
    }

    public Appraisal submitSelfReview(Long appraisalId, List<RatingDto> ratings) {
        Appraisal appraisal = getUnlockedAppraisal(appraisalId);
        applyRatings(ratings, true);
        appraisal.setStatus(AppraisalStatus.SELF_REVIEW);
        return appraisalRepository.save(appraisal);
    }

    public Appraisal submitManagerReview(Long appraisalId, List<RatingDto> ratings) {
        Appraisal appraisal = getUnlockedAppraisal(appraisalId);
        applyRatings(ratings, false);
        appraisal.setStatus(AppraisalStatus.MANAGER_REVIEW);
        return appraisalRepository.save(appraisal);
    }

    public Appraisal finalizeAppraisal(Long appraisalId, String finalizedBy) {
        Appraisal appraisal = getUnlockedAppraisal(appraisalId);

        double totalScore = 0.0;
        for (KpiScore kpi : appraisal.getKpiScores()) {
            int self = kpi.getSelfRating() != null ? kpi.getSelfRating() : 0;
            int mgr = kpi.getManagerRating() != null ? kpi.getManagerRating() : 0;
            double blended = (0.3 * self) + (0.7 * mgr);
            double normalized = (blended / 5.0) * 100.0;
            totalScore += normalized * kpi.getWeight();
        }

        appraisal.setFinalScore(Math.round(totalScore * 100) / 100.0);
        appraisal.setStatus(AppraisalStatus.FINALIZED);
        appraisal.setLocked(true);
        Appraisal saved = appraisalRepository.save(appraisal);

        AppraisalAuditLog log = new AppraisalAuditLog();
        log.setAppraisalId(saved.getId());
        log.setEmployeeId(saved.getEmployee().getId());
        log.setFinalScore(saved.getFinalScore());
        log.setFinalizedAt(LocalDateTime.now());
        log.setFinalizedBy(finalizedBy);
        auditLogRepository.save(log);

        return saved;
    }

    public List<Appraisal> getAppraisalsForEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        return appraisalRepository.findByEmployee(employee);
    }

    private void applyRatings(List<RatingDto> ratings, boolean self) {
        for (RatingDto r : ratings) {
            KpiScore score = kpiScoreRepository.findById(r.getKpiScoreId())
                    .orElseThrow(() -> new IllegalArgumentException("KPI score not found: " + r.getKpiScoreId()));
            if (self) {
                score.setSelfRating(r.getRating());
            } else {
                score.setManagerRating(r.getRating());
            }
            kpiScoreRepository.save(score);
        }
    }

    private Appraisal getUnlockedAppraisal(Long appraisalId) {
        Appraisal appraisal = appraisalRepository.findById(appraisalId)
                .orElseThrow(() -> new IllegalArgumentException("Appraisal not found"));
        if (appraisal.isLocked()) {
            throw new IllegalStateException("Appraisal is finalized and locked; it cannot be modified");
        }
        return appraisal;
    }
}

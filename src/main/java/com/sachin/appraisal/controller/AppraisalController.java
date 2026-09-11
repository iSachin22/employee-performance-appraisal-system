package com.sachin.appraisal.controller;

import com.sachin.appraisal.dto.KpiGoalDto;
import com.sachin.appraisal.dto.RatingDto;
import com.sachin.appraisal.entity.Appraisal;
import com.sachin.appraisal.service.AppraisalService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appraisal")
public class AppraisalController {

    private final AppraisalService appraisalService;

    public AppraisalController(AppraisalService appraisalService) {
        this.appraisalService = appraisalService;
    }

    @PostMapping("/start/{employeeId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('HR_ADMIN')")
    public Appraisal startAppraisal(@PathVariable Long employeeId,
                                     @RequestParam String reviewCycle,
                                     @Valid @RequestBody List<KpiGoalDto> goals) {
        return appraisalService.startAppraisal(employeeId, reviewCycle, goals);
    }

    @PostMapping("/{appraisalId}/self-review")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
    public Appraisal submitSelfReview(@PathVariable Long appraisalId, @RequestBody List<RatingDto> ratings) {
        return appraisalService.submitSelfReview(appraisalId, ratings);
    }

    @PostMapping("/{appraisalId}/manager-review")
    @PreAuthorize("hasRole('MANAGER')")
    public Appraisal submitManagerReview(@PathVariable Long appraisalId, @RequestBody List<RatingDto> ratings) {
        return appraisalService.submitManagerReview(appraisalId, ratings);
    }

    @PostMapping("/{appraisalId}/finalize")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public Appraisal finalizeAppraisal(@PathVariable Long appraisalId, Authentication authentication) {
        return appraisalService.finalizeAppraisal(appraisalId, authentication.getName());
    }

    @GetMapping("/employee/{employeeId}")
    public List<Appraisal> getForEmployee(@PathVariable Long employeeId) {
        return appraisalService.getAppraisalsForEmployee(employeeId);
    }
}

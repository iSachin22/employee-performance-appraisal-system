package com.sachin.appraisal.service;

import com.sachin.appraisal.dto.KpiGoalDto;
import com.sachin.appraisal.entity.*;
import com.sachin.appraisal.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppraisalServiceTest {

    @Mock private AppraisalRepository appraisalRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private KpiScoreRepository kpiScoreRepository;
    @Mock private AppraisalAuditLogRepository auditLogRepository;

    private AppraisalService appraisalService;
    private Employee employee;

    @BeforeEach
    void setUp() {
        appraisalService = new AppraisalService(
                appraisalRepository, employeeRepository, kpiScoreRepository, auditLogRepository);
        employee = new Employee(1L, "emp@company.com", "pass", "Employee One", Role.EMPLOYEE, null);
    }

    @Test
    void startAppraisal_throws_whenWeightsDoNotSumToOne() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        KpiGoalDto goal = new KpiGoalDto();
        goal.setKpiName("Code Quality");
        goal.setWeight(0.5);

        assertThrows(IllegalArgumentException.class,
                () -> appraisalService.startAppraisal(1L, "2026-H1", List.of(goal)));
    }

    @Test
    void startAppraisal_succeeds_whenWeightsSumToOne() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(appraisalRepository.save(any(Appraisal.class))).thenAnswer(inv -> {
            Appraisal a = inv.getArgument(0);
            a.setId(100L);
            return a;
        });

        KpiGoalDto goal1 = new KpiGoalDto();
        goal1.setKpiName("Code Quality");
        goal1.setWeight(0.6);
        KpiGoalDto goal2 = new KpiGoalDto();
        goal2.setKpiName("Communication");
        goal2.setWeight(0.4);

        Appraisal result = appraisalService.startAppraisal(1L, "2026-H1", List.of(goal1, goal2));

        assertEquals(AppraisalStatus.GOAL_SETTING, result.getStatus());
        verify(kpiScoreRepository, times(2)).save(any(KpiScore.class));
    }

    @Test
    void finalizeAppraisal_computesWeightedNormalizedScore() {
        Appraisal appraisal = new Appraisal();
        appraisal.setId(200L);
        appraisal.setEmployee(employee);
        appraisal.setStatus(AppraisalStatus.MANAGER_REVIEW);

        KpiScore kpi1 = new KpiScore(1L, appraisal, "Code Quality", 0.6, 4, 5);
        KpiScore kpi2 = new KpiScore(2L, appraisal, "Communication", 0.4, 3, 4);
        appraisal.setKpiScores(List.of(kpi1, kpi2));

        when(appraisalRepository.findById(200L)).thenReturn(Optional.of(appraisal));
        when(appraisalRepository.save(any(Appraisal.class))).thenAnswer(inv -> inv.getArgument(0));
        when(auditLogRepository.save(any(AppraisalAuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

        Appraisal result = appraisalService.finalizeAppraisal(200L, "hr@company.com");

        assertEquals(86.0, result.getFinalScore(), 0.01);
        assertEquals(AppraisalStatus.FINALIZED, result.getStatus());
        assertTrue(result.isLocked());
        verify(auditLogRepository).save(any(AppraisalAuditLog.class));
    }

    @Test
    void finalizeAppraisal_throws_whenAlreadyLocked() {
        Appraisal appraisal = new Appraisal();
        appraisal.setId(300L);
        appraisal.setLocked(true);

        when(appraisalRepository.findById(300L)).thenReturn(Optional.of(appraisal));

        assertThrows(IllegalStateException.class,
                () -> appraisalService.finalizeAppraisal(300L, "hr@company.com"));
    }
}

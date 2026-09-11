package com.sachin.appraisal.repository;

import com.sachin.appraisal.entity.AppraisalAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppraisalAuditLogRepository extends JpaRepository<AppraisalAuditLog, Long> {
}

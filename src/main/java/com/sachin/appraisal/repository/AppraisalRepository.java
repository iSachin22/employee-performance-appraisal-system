package com.sachin.appraisal.repository;

import com.sachin.appraisal.entity.Appraisal;
import com.sachin.appraisal.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppraisalRepository extends JpaRepository<Appraisal, Long> {
    List<Appraisal> findByEmployee(Employee employee);
}

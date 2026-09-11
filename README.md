# Employee Performance & Appraisal Management System

A role-based appraisal management system (Employee, Manager, HR Admin) built with **Spring Boot, Hibernate, and MySQL**, supporting goal-setting, self-review, and manager-review workflows.

## Features
- Goal-setting phase with weighted KPIs (weights must sum to 1.0 per appraisal)
- Self-review and manager-review workflows with 1-5 ratings per KPI
- Weighted rating-aggregation engine: blends self (30%) and manager (70%) ratings per KPI, normalizes to a 0-100 scale, aggregates by KPI weight
- Immutable audit trail: once finalized, the appraisal is locked and a permanent audit log entry is written
- REST APIs secured with Spring Security + JWT, role-based authorization

## Tech Stack
- Java 17, Spring Boot 3, Spring Data JPA (Hibernate), Spring Security, JJWT, MySQL, Maven, JUnit 5 + Mockito

## Running Locally
1. Create a MySQL database `appraisal_db`
2. Update credentials and `jwt.secret` in `src/main/resources/application.properties`
3. `mvn spring-boot:run`

## Running Tests
```
mvn test
```

## Author
**Sachin Rathod** — MCA Student | Aspiring Java Developer

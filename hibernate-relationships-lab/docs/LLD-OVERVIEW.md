# Hibernate Relationships Lab - LLD Overview

## Purpose
This module demonstrates all four Hibernate/JPA associations in one domain:
- `@OneToOne`
- `@OneToMany`
- `@ManyToOne`
- `@ManyToMany`

Domain used: Hospital system.

## Package Layout
```text
com.example.hibernaterelationshipslab
|
+-- controller
|   +-- AppointmentController
|   +-- DoctorController
|   +-- PatientController
|   +-- SpecialtyController
|
+-- dto
|   +-- request and response DTOs
|
+-- entity
|   +-- Appointment
|   +-- AppointmentStatus
|   +-- Doctor
|   +-- InsuranceCard
|   +-- Patient
|   +-- Specialty
|
+-- exception
|   +-- GlobalExceptionHandler
|   +-- RelationshipConflictException
|   +-- ResourceNotFoundException
|
+-- repository
|   +-- AppointmentRepository
|   +-- DoctorRepository
|   +-- InsuranceCardRepository
|   +-- PatientRepository
|   +-- SpecialtyRepository
|
+-- service
    +-- AppointmentService
    +-- DoctorService
    +-- PatientService
    +-- SpecialtyService
```

## Runtime Profiles
- `application.properties` sets active profile to `local`.
- `application-local.properties` uses MySQL.
- `application-test.properties` uses H2 in-memory for integration tests.

## Learning Checklist
1. Create `Patient` and attach one `InsuranceCard` (`@OneToOne`).
2. Create `Appointment` between `Patient` and `Doctor` (`@ManyToOne` and `@OneToMany`).
3. Link and unlink `Doctor` to `Specialty` (`@ManyToMany` with join table).
4. Review SQL logs to observe FK and join table statements.

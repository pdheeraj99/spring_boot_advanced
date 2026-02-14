# LLD - Validation and Errors

## Validation Rules
```text
POST /api/patients
- fullName: required
- email: required, valid email format

POST /api/patients/{id}/insurance-card
- providerName: required
- policyNumber: required
- validTill: required, future date
- business rule: patient can have only one insurance card

POST /api/doctors
- fullName: required
- licenseNumber: required

POST /api/specialties
- code: required
- name: required

POST /api/appointments
- doctorId: required
- patientId: required
- appointmentAt: required, future datetime
- reason: required
```

## Error Mapping
```text
Exception Type                    HTTP
ResourceNotFoundException         404 Not Found
RelationshipConflictException     409 Conflict
MethodArgumentNotValidException   400 Bad Request
```

## Failure Scenarios
```text
1) Attach second insurance card to same patient
   -> 409 Conflict

2) Create appointment with missing doctor/patient
   -> 404 Not Found

3) Add same specialty link twice for a doctor
   -> 409 Conflict

4) Invalid payload (blank fields, invalid email, past date)
   -> 400 Bad Request
```

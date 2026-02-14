# LLD - Test Matrix

## Automated Integration Scenarios
```text
ID   Scenario                                              Expected
T1   Create patient                                        201 + patient id
T2   Add first insurance card to patient                   201 + card payload
T3   Add second insurance card to same patient             409 conflict
T4   Create doctor                                         201 + doctor id
T5   Create appointment with valid doctor and patient      201 + doctorId/patientId in response
T6   Create appointment with missing doctor                404 not found
T7   Create specialty                                      201 + specialty id
T8   Link doctor to specialty                              200 + specialty appears in doctor response
T9   Link same doctor-specialty again                      409 conflict
T10  Get doctors by specialty after linking                doctors list size = 1
T11  Unlink doctor-specialty                               204 no content
T12  Get doctors by specialty after unlink                 doctors list size = 0
T13  Get doctor appointments                               list contains created appointment
T14  Get patient detail                                    includes appointments array
```

## Manual Verification Checklist
```text
1) Run app with local profile (MySQL).
2) Execute endpoint sequence for all associations.
3) Inspect SQL logs:
   - insert/update on appointments doctor_id/patient_id
   - insert/delete on doctor_specialties join table
   - unique constraint behavior on insurance_cards.patient_id
4) Confirm no lazy serialization exception in API responses.
```

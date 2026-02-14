# LLD - Entity Relationships

## ER View (ASCII)
```text
PATIENT (1) ------------------------- (1) INSURANCE_CARD
   id  <------------------------ patient_id (UNIQUE, NOT NULL)
   full_name                                  provider_name
   email (UNIQUE)                             policy_number (UNIQUE)

DOCTOR (1) --------------------------- (*) APPOINTMENT (*) --------------------------- (1) PATIENT
   id  <------------------------ doctor_id (NOT NULL)   patient_id (NOT NULL) ------> id
   full_name                                   appointment_at                          full_name
   license_number (UNIQUE)                     status                                  email
                                              reason

DOCTOR (*) -------------------------- (*) SPECIALTY
   id          doctor_specialties         id
               doctor_id (FK)             code (UNIQUE)
               specialty_id (FK)          name
```

## Cardinality Table
```text
Relationship                           Cardinality    Owning Side
Patient <-> InsuranceCard              1 : 1          InsuranceCard (JoinColumn patient_id)
Doctor <-> Appointment                 1 : many       Appointment (doctor_id)
Patient <-> Appointment                1 : many       Appointment (patient_id)
Doctor <-> Specialty                   many : many    Doctor (JoinTable doctor_specialties)
```

## Ownership and mappedBy
```text
Patient.insuranceCard          -> inverse side (mappedBy = "patient")
InsuranceCard.patient          -> owning side (@JoinColumn patient_id)

Doctor.appointments            -> inverse side (mappedBy = "doctor")
Appointment.doctor             -> owning side (@JoinColumn doctor_id)

Patient.appointments           -> inverse side (mappedBy = "patient")
Appointment.patient            -> owning side (@JoinColumn patient_id)

Doctor.specialties             -> owning side (@JoinTable doctor_specialties)
Specialty.doctors              -> inverse side (mappedBy = "specialties")
```

## Cascade and orphanRemoval Decisions
```text
Patient -> InsuranceCard       cascade=ALL, orphanRemoval=true
Reason: card lifecycle is fully bound to patient in this lab.

Doctor -> Appointment          cascade=ALL, orphanRemoval=true
Patient -> Appointment         cascade=ALL, orphanRemoval=true
Reason: appointments are aggregate children for learning convenience.

Doctor <-> Specialty           no cascade remove
Reason: removing a link must not delete shared specialty rows.
```

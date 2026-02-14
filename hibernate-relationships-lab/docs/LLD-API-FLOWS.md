# LLD - API Flows

## 1) Create Patient + Insurance Card
```text
Client
  |
  | POST /api/patients
  v
PatientController -> PatientService -> PatientRepository -> DB(patients)
  |
  | POST /api/patients/{id}/insurance-card
  v
PatientController -> PatientService -> PatientRepository -> DB(insurance_cards with unique patient_id)
```

Example requests:
```http
POST /api/patients
Content-Type: application/json

{
  "fullName": "Anita Rao",
  "email": "anita.rao@example.com"
}
```

```http
POST /api/patients/1/insurance-card
Content-Type: application/json

{
  "providerName": "ShieldCare",
  "policyNumber": "POL-1001",
  "validTill": "2030-12-31"
}
```

## 2) Create Appointment (ManyToOne + OneToMany)
```text
Client
  |
  | POST /api/appointments
  v
AppointmentController
  -> AppointmentService
      -> DoctorRepository.findById
      -> PatientRepository.findById
      -> AppointmentRepository.save
          DB(appointments with doctor_id + patient_id)
```

## 3) Link Doctor to Specialty (ManyToMany)
```text
Client
  |
  | POST /api/doctors/{doctorId}/specialties/{specialtyId}
  v
DoctorController -> DoctorService
  -> DoctorRepository.findById
  -> SpecialtyRepository.findById
  -> doctor.addSpecialty(specialty)
  -> DoctorRepository.save
     DB(doctor_specialties insert)
```

## 4) Unlink Doctor from Specialty
```text
Client
  |
  | DELETE /api/doctors/{doctorId}/specialties/{specialtyId}
  v
DoctorController -> DoctorService
  -> doctor.removeSpecialty(specialty)
  -> DoctorRepository.save
     DB(doctor_specialties delete only)
```

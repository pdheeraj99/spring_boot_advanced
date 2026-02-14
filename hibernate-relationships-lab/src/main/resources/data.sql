INSERT INTO patients (id, full_name, email) VALUES (1, 'Alice Carter', 'alice.carter@example.com');
INSERT INTO patients (id, full_name, email) VALUES (2, 'Brian Scott', 'brian.scott@example.com');

INSERT INTO doctors (id, full_name, license_number) VALUES (1, 'Dr. Emma Stone', 'LIC-NEURO-001');
INSERT INTO doctors (id, full_name, license_number) VALUES (2, 'Dr. Noah Lee', 'LIC-CARD-002');

INSERT INTO specialties (id, code, name) VALUES (1, 'CARD', 'Cardiology');
INSERT INTO specialties (id, code, name) VALUES (2, 'NEUR', 'Neurology');
INSERT INTO specialties (id, code, name) VALUES (3, 'ORTH', 'Orthopedics');

INSERT INTO doctor_specialties (doctor_id, specialty_id) VALUES (1, 2);
INSERT INTO doctor_specialties (doctor_id, specialty_id) VALUES (2, 1);

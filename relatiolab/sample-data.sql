-- Sample data for RelatioLab
INSERT INTO students (name, email, created_at) VALUES
('Ravi', 'ravi@relatiolab.dev', NOW()),
('Sita', 'sita@relatiolab.dev', NOW());

INSERT INTO student_profiles (student_id, phone, address, linkedin_url) VALUES
(1, '9000011111', 'Hyderabad', 'https://linkedin.com/in/ravi'),
(2, '9000022222', 'Vijayawada', 'https://linkedin.com/in/sita');

INSERT INTO courses (title, fee, active) VALUES
('Spring Data JPA Deep Dive', 2500, true),
('Hibernate Performance Mastery', 3000, true);

INSERT INTO enrollments (student_id, course_id, enrolled_at, progress_percent, status) VALUES
(1, 1, NOW(), 25, 'ACTIVE'),
(1, 2, NOW(), 10, 'ACTIVE'),
(2, 1, NOW(), 80, 'ACTIVE');

INSERT INTO mentors (name, expertise_level) VALUES
('Anil', 'SENIOR'),
('Divya', 'LEAD');

INSERT INTO skills (code, display_name) VALUES
('JPA', 'Spring Data JPA'),
('HIB', 'Hibernate Tuning'),
('SQL', 'SQL Optimization');

INSERT INTO mentor_courses (course_id, mentor_id) VALUES
(1, 1),
(2, 1),
(2, 2);

INSERT INTO mentor_skills (mentor_id, skill_id) VALUES
(1, 1),
(1, 2),
(2, 2),
(2, 3);
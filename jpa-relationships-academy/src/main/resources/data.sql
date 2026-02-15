-- ============================================
-- SEED DATA for JPA Relationships Academy
-- ============================================
-- OneToOne: Husband ↔ Wife
INSERT INTO husbands (id, name, age)
VALUES (1, 'Ravi Kumar', 30);
INSERT INTO husbands (id, name, age)
VALUES (2, 'Anil Reddy', 35);
INSERT INTO husbands (id, name, age)
VALUES (3, 'Suresh Babu', 28);
INSERT INTO wives (id, name, age, husband_id)
VALUES (1, 'Priya Kumari', 28, 1);
INSERT INTO wives (id, name, age, husband_id)
VALUES (2, 'Swathi Reddy', 32, 2);
-- OneToMany: Mother → Children
INSERT INTO mothers (id, name, age)
VALUES (1, 'Lakshmi Devi', 45);
INSERT INTO mothers (id, name, age)
VALUES (2, 'Saraswathi Amma', 50);
INSERT INTO children (id, name, age, mother_id)
VALUES (1, 'Rahul', 20, 1);
INSERT INTO children (id, name, age, mother_id)
VALUES (2, 'Rohit', 18, 1);
INSERT INTO children (id, name, age, mother_id)
VALUES (3, 'Sneha', 15, 1);
INSERT INTO children (id, name, age, mother_id)
VALUES (4, 'Kiran', 22, 2);
INSERT INTO children (id, name, age, mother_id)
VALUES (5, 'Divya', 19, 2);
-- ManyToMany: Student ↔ Course
INSERT INTO students (id, name, roll_number)
VALUES (1, 'Arjun Patel', 'CS-001');
INSERT INTO students (id, name, roll_number)
VALUES (2, 'Meena Sharma', 'CS-002');
INSERT INTO students (id, name, roll_number)
VALUES (3, 'Vikram Singh', 'EC-001');
INSERT INTO courses (id, name, code)
VALUES (1, 'Data Structures', 'DSA-101');
INSERT INTO courses (id, name, code)
VALUES (2, 'Database Systems', 'DB-201');
INSERT INTO courses (id, name, code)
VALUES (3, 'Web Development', 'WEB-301');
INSERT INTO student_courses (student_id, course_id)
VALUES (1, 1);
INSERT INTO student_courses (student_id, course_id)
VALUES (1, 2);
INSERT INTO student_courses (student_id, course_id)
VALUES (2, 1);
INSERT INTO student_courses (student_id, course_id)
VALUES (2, 3);
INSERT INTO student_courses (student_id, course_id)
VALUES (3, 2);
INSERT INTO student_courses (student_id, course_id)
VALUES (3, 3);
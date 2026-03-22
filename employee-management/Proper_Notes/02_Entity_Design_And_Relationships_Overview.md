# 🏗️ 02. Entity Design & Relationships Overview

Mawa, mana project lo **4 Entities** unnayi. Vaati madhya **3 types** of JPA relationships unnayi. Ee note lo motham entities ni bird's eye view ga chuddam — database lo tables ela kanipistayo, relationships ela connect avutayo.

---

## 📊 Entity Summary Table

| Entity | Table Name | Key Fields | Role in Relationships |
|---|---|---|---|
| **Employee** | `employees` | id, name, email | Central entity — anni relationships ki connected |
| **Department** | `departments` | id, name, location | Employee ki parent (One-to-Many) |
| **Passport** | `passports` | id, passport_number, issued_country | Employee ki identity (One-to-One) |
| **Project** | `projects` | id, name, deadline | Employee tho Many-to-Many |

---

## 🔗 Relationship Summary

| Relationship Type | Between | Owner (FK Holder) | Inverse (mappedBy) | FK/Join Table |
|---|---|---|---|---|
| **@OneToOne** | Employee ↔ Passport | `Passport` | `Employee` | `passports.employee_id` |
| **@OneToMany / @ManyToOne** | Department ↔ Employee | `Employee` | `Department` | `employees.department_id` |
| **@ManyToMany** | Employee ↔ Project | `Project` | `Employee` | `employee_projects` (join table) |

---

## 🖼️ ER Diagram (ASCII)

```text
                         +----------------+
                         |  DEPARTMENTS   |
                         +----------------+
                         | id       (PK)  |
                         | name           |
                         | location       |
                         +-------+--------+
                                 |
                                 | 1
                                 |
                                 | N
                         +-------+--------+
  +-------------+        |   EMPLOYEES    |        +------------------+
  |  PASSPORTS  |        +----------------+        | EMPLOYEE_PROJECTS|
  +-------------+   1    | id       (PK)  |    N   +------------------+
  | id    (PK)  |--------| name           |--------| employee_id (FK) |
  | passport_no |   1    | email          |    N   | project_id  (FK) |
  | country     |        | department_id  |        +--------+---------+
  | employee_id |        |   (FK)         |                 |
  |  (FK)       |        +----------------+                 | N
  +-------------+                                           |
                                                     +------+-------+
                                                     |   PROJECTS   |
                                                     +--------------+
                                                     | id     (PK)  |
                                                     | name         |
                                                     | deadline     |
                                                     +--------------+
```

---

## 🗄️ Database Tables View (Data Example)

### Table: DEPARTMENTS

```text
+----+--------+------------+
| ID | NAME   | LOCATION   |
+----+--------+------------+
| 10 | IT     | Hyderabad  |
| 20 | HR     | Bangalore  |
+----+--------+------------+
```

### Table: EMPLOYEES

```text
+----+--------+---------------------+---------------+
| ID | NAME   | EMAIL               | DEPARTMENT_ID |  ← FK to departments
+----+--------+---------------------+---------------+
| 1  | Ram    | ram@example.com     | 10            |
| 2  | Sita   | sita@example.com    | 10            |
| 3  | Ravi   | ravi@example.com    | 20            |
+----+--------+---------------------+---------------+
```

### Table: PASSPORTS

```text
+-----+-----------------+----------+-------------+
| ID  | PASSPORT_NUMBER | COUNTRY  | EMPLOYEE_ID |  ← FK to employees
+-----+-----------------+----------+-------------+
| 100 | P12345          | INDIA    | 1           |
| 101 | P67890          | USA      | 2           |
+-----+-----------------+----------+-------------+
```

### Table: PROJECTS

```text
+----+--------+------------+
| ID | NAME   | DEADLINE   |
+----+--------+------------+
| 50 | Java   | 2026-06-15 |
| 60 | React  | 2026-08-20 |
+----+--------+------------+
```

### Table: EMPLOYEE_PROJECTS (Join Table — Auto Created)

```text
+------------+-------------+
| PROJECT_ID | EMPLOYEE_ID |
+------------+-------------+
| 50         | 1           |  ← Ram in Java project
| 50         | 2           |  ← Sita in Java project
| 60         | 1           |  ← Ram in React project
+------------+-------------+
```

---

## 🎯 Key Observations

1. **Employee is the CENTER** — anni relationships ki connected. Department tho, Passport tho, Project tho.
2. **Foreign Key always "Many" side lo untundi** —
   - `employees` table lo `department_id` (Many employees → One department)
   - `passports` table lo `employee_id` (decision based — Passport lo pettam)
3. **Many-to-Many ki separate join table** — `employee_projects` because Employee lo `project_id` pedithe okka project eh possible, Project lo `employee_id` pedithe okka employee eh possible. Anduke third table!
4. **5 tables total** — 4 entity tables + 1 join table

---

**Next Note:** [03_OneToOne_Employee_Passport.md](./03_OneToOne_Employee_Passport.md) — One-to-One deep-dive 🔗

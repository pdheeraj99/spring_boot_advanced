# 🧪 10. API Endpoints & Testing Guide

Mawa, ee note lo mana application lo unna **anni API endpoints** ni organized ga chuddham. Postman tho test cheyyadaniki ready-made requests kooda istanu!

---

## 📋 Complete API Endpoints Reference

| # | Method | Endpoint | Purpose | Response |
|---|---|---|---|---|
| 1 | **POST** | `/api/departments` | Create a new department | DepartmentDto |
| 2 | **POST** | `/api/departments/{deptId}/employees` | Add employee to department | DepartmentDto |
| 3 | **GET** | `/api/departments` | Get all departments with employees | List\<DepartmentDto\> |
| 4 | **POST** | `/api/employees` | Create a new employee | EmployeeDetailDto |
| 5 | **POST** | `/api/employees/{id}/passport` | Add passport to employee | MessageDto |
| 6 | **GET** | `/api/employees/{id}` | Get employee full details | EmployeeDetailDto |
| 7 | **GET** | `/api/employees/{id}/projects` | Get employee's projects | List\<ProjectSummaryDto\> |
| 8 | **POST** | `/api/projects` | Create a new project | ProjectDto |
| 9 | **GET** | `/api/projects` | Get all projects with employees | List\<ProjectDto\> |
| 10 | **GET** | `/api/projects/{id}` | Get single project | ProjectDto |
| 11 | **POST** | `/api/projects/{projectId}/employees/{employeeId}` | Link employee to project | MessageDto |
| 12 | **DELETE** | `/api/projects/{projectId}/employees/{employeeId}` | Unlink employee from project | MessageDto |

---

## 🚀 Testing Order (Step-by-Step)

Follow this exact order for clean testing:

### Step 1: Create a Department

```http
POST http://localhost:8082/api/departments
Content-Type: application/json

{
    "name": "IT",
    "location": "Hyderabad"
}
```

**Expected Response:**

```json
{
    "id": 1,
    "name": "IT",
    "location": "Hyderabad",
    "employees": []
}
```

---

### Step 2: Add Employee to Department

```http
POST http://localhost:8082/api/departments/1/employees
Content-Type: application/json

{
    "name": "Ram",
    "email": "ram@example.com"
}
```

**Expected Response:**

```json
{
    "id": 1,
    "name": "IT",
    "location": "Hyderabad",
    "employees": [
        {
            "id": 1,
            "name": "Ram",
            "email": "ram@example.com"
        }
    ]
}
```

> **Internally em jarugutundi?**
>
> - `department.addEmployee(employee)` call avtundi (Helper method!)
> - Employee ki department auto-set avtundi (bidirectional sync)
> - `departmentRepository.save()` both department and employee save chestundi (Cascade!)

---

### Step 3: Add another Employee

```http
POST http://localhost:8082/api/departments/1/employees
Content-Type: application/json

{
    "name": "Sita",
    "email": "sita@example.com"
}
```

---

### Step 4: Add Passport to Employee

```http
POST http://localhost:8082/api/employees/1/passport
Content-Type: application/json

{
    "passportNumber": "IND12345",
    "issuedCountry": "India"
}
```

**Expected Response:**

```json
{
    "message": "Passport added successfully via Cascade!"
}
```

> **Internally em jarugutundi?**
>
> - `employee.setPassport(passport)` call avtundi (Smart setter!)
> - Passport ki employee auto-set avtundi (bidirectional sync)
> - `employeeRepository.save()` passport ni kooda save chestundi (CascadeType.ALL!)

---

### Step 5: Get Employee Details

```http
GET http://localhost:8082/api/employees/1
```

**Expected Response:**

```json
{
    "id": 1,
    "name": "Ram",
    "email": "ram@example.com",
    "departmentName": "IT",
    "passport": {
        "id": 1,
        "passportNumber": "IND12345",
        "issuedCountry": "India"
    },
    "projects": []
}
```

> **Internally:** `findDetailsById()` uses `@EntityGraph` → Single JOIN query lo Department + Passport + Projects anni vasthay!

---

### Step 6: Create a Project

```http
POST http://localhost:8082/api/projects
Content-Type: application/json

{
    "name": "Java Microservices",
    "deadline": "2026-06-15"
}
```

**Expected Response:**

```json
{
    "id": 1,
    "name": "Java Microservices",
    "deadline": "2026-06-15",
    "employees": []
}
```

---

### Step 7: Link Employee to Project

```http
POST http://localhost:8082/api/projects/1/employees/1
```

**Expected Response:**

```json
{
    "message": "Employee linked to project successfully"
}
```

> **Internally em jarugutundi?**
>
> - Duplicate check — already linked aithe skip chestundi
> - `project.addEmployee(employee)` call avtundi (Helper method!)
> - `employee_projects` join table lo new row insert avtundi

---

### Step 8: Verify — Get Employee Projects

```http
GET http://localhost:8082/api/employees/1/projects
```

**Expected Response:**

```json
[
    {
        "id": 1,
        "name": "Java Microservices",
        "deadline": "2026-06-15"
    }
]
```

---

### Step 9: Get All Departments

```http
GET http://localhost:8082/api/departments
```

**Expected Response:**

```json
[
    {
        "id": 1,
        "name": "IT",
        "location": "Hyderabad",
        "employees": [
            { "id": 1, "name": "Ram", "email": "ram@example.com" },
            { "id": 2, "name": "Sita", "email": "sita@example.com" }
        ]
    }
]
```

> **Internally:** `findAllWithEmployees()` uses `@EntityGraph` → Single JOIN query! N+1 problem avoided! 🚀

---

### Step 10: Unlink Employee from Project

```http
DELETE http://localhost:8082/api/projects/1/employees/1
```

**Expected Response:**

```json
{
    "message": "Employee unlinked from project successfully"
}
```

> **Internally:** `project.removeEmployee(employee)` → join table nundi row delete avtundi. Employee and Project records safe — just link cut avtundi.

---

## 🗄️ Database After All Steps

```text
Table: DEPARTMENTS
+----+------+------------+
| ID | NAME | LOCATION   |
+----+------+------------+
| 1  | IT   | Hyderabad  |
+----+------+------------+

Table: EMPLOYEES
+----+------+--------------------+---------------+
| ID | NAME | EMAIL              | DEPARTMENT_ID |
+----+------+--------------------+---------------+
| 1  | Ram  | ram@example.com    | 1             |
| 2  | Sita | sita@example.com   | 1             |
+----+------+--------------------+---------------+

Table: PASSPORTS
+----+-----------------+---------+-------------+
| ID | PASSPORT_NUMBER | COUNTRY | EMPLOYEE_ID |
+----+-----------------+---------+-------------+
| 1  | IND12345        | India   | 1           |
+----+-----------------+---------+-------------+

Table: PROJECTS
+----+--------------------+------------+
| ID | NAME               | DEADLINE   |
+----+--------------------+------------+
| 1  | Java Microservices | 2026-06-15 |
+----+--------------------+------------+

Table: EMPLOYEE_PROJECTS
+------------+-------------+
| PROJECT_ID | EMPLOYEE_ID |
+------------+-------------+
| (empty after unlink)     |
+------------+-------------+
```

---

## 📊 Concepts Used in Each Endpoint (Quick Map)

| Endpoint | Concepts Demonstrated |
|---|---|
| Create Department | Basic JPA save |
| Add Employee to Dept | **Helper method**, **Cascade**, **Bidirectional sync** |
| Add Passport | **Smart setter**, **CascadeType.ALL**, **OneToOne** |
| Get Employee | **@EntityGraph**, **DTO mapping**, **Single query fetch** |
| Create Project | Basic JPA save, **LocalDate** |
| Link Employee-Project | **ManyToMany**, **Join table**, **Duplicate check** |
| Unlink Employee-Project | **Helper remove**, **Join table row delete** |
| Get All Departments | **@EntityGraph**, **N+1 prevention**, **`distinct`** |

---

## 🎯 Key Takeaways

1. **Testing order matters** — Department → Employee → Passport → Project → Link
2. **Helper methods** (addEmployee, setPassport) ensure bidirectional sync
3. **Cascade** saves related entities automatically — no need for separate save calls
4. **@EntityGraph** methods used everywhere in controllers — N+1 prevented!
5. **DTOs** ensure clean API responses without infinite loops
6. **Duplicate check** in link endpoint prevents same employee-project link twice
7. **Unlink** only removes join table row — original entities stay safe

---

## 🏆 Congratulations Mawa

Ee 10 notes complete chesthe, nuvvu:

- ✅ JPA Relationships (OneToOne, OneToMany, ManyToMany) master!
- ✅ Owner vs Inverse crystal clear!
- ✅ Lazy vs Eager loading pro!
- ✅ N+1 Problem detect & solve cheyagalav!
- ✅ DTOs, Mapper, Controllers — full stack JPA app build cheyagalav!

**Interview lo evaraina evi adigina confident ga explain cheyochu!** 🔥😎

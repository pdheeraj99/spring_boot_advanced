# 🚀 08. N+1 Problem & @EntityGraph

Mawa, `@EntityGraph` lekapothe em jarugutundo telusukunte, dani viluva neeku inka baga artham avtundi. **N+1 Problem** anedi production lo applications ni slow chese **#1 killer**!

---

## 💥 The N+1 Problem — Enti Idi?

**Simple Definition:** Neeku N items kavali. Database ki **1 query** to get the items + **N queries** to get each item's related data = **N+1 total queries!**

### Real Example (Mana Code tho)

Imagine database lo **5 Departments** unnayi, prathi department lo **10 Employees** unnaru.

Nuvvu `departmentRepository.findAll()` call chesi, loop lo employees access chestunnav:

```java
// ❌ WITHOUT @EntityGraph
List<Department> depts = departmentRepository.findAll();

for (Department dept : depts) {
    System.out.println(dept.getName() + ": " + dept.getEmployees().size());
    // ↑ Prathi iteration lo DB call veltundi! (LAZY trigger)
}
```

**Queries that run:**

```sql
-- Query 1: Get all departments
SELECT * FROM departments;
-- Result: 5 departments (IT, HR, Sales, Admin, Ops)

-- Query 2: LAZY trigger for IT dept
SELECT * FROM employees WHERE department_id = 1;

-- Query 3: LAZY trigger for HR dept
SELECT * FROM employees WHERE department_id = 2;

-- Query 4: LAZY trigger for Sales dept
SELECT * FROM employees WHERE department_id = 3;

-- Query 5: LAZY trigger for Admin dept
SELECT * FROM employees WHERE department_id = 4;

-- Query 6: LAZY trigger for Ops dept
SELECT * FROM employees WHERE department_id = 5;
```

**Total: 6 queries!** (1 + 5 = N + 1)

100 departments unte? → **101 Queries!** 💥
1000 departments unte? → **1001 Queries!** → System CRASH! 🔥

---

## 📊 ASCII Visual: The Problem

### ❌ Without @EntityGraph (Traffic Jam)

```text
Application                    Database
    |                             |
    |--- Give me all Depts ----->| (Query 1)
    |<-- Here are 5 Depts -------|
    |                             |
    |--- Emps for Dept 1 ------->| (Query 2 - Latency...)
    |<------- Employees ---------|
    |                             |
    |--- Emps for Dept 2 ------->| (Query 3 - Latency...)
    |<------- Employees ---------|
    |                             |
    |--- Emps for Dept 3 ------->| (Query 4 - Latency...)
    |<------- Employees ---------|
    .                             .
    .    (Repeats N times)        .
```

### ✅ With @EntityGraph (Express Highway)

```text
Application                    Database
    |                             |
    |--- Give me EVERYTHING ----->| (Single Smart JOIN Query)
    |<-- Depts + Emps (Packed) ---|
    |                             |
    DONE! 🚀
```

---

## ✅ The Solution: @EntityGraph

`@EntityGraph` anedi Hibernate ki manam icche **instruction manual**:

> *"Orey Hibernate, naku telusu nuvvu LAZY ga untav ani. Kani eesari matram naku related data kooda kavali. Please anni kalipi okesari (EAGER fetch) teesko!"*

---

## 📝 Mana Repository Code (All 3 Repositories)

### DepartmentRepository.java

```java
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // ✅ All departments + their employees (Single query!)
    @EntityGraph(attributePaths = "employees")
    @Query("select distinct d from Department d")
    List<Department> findAllWithEmployees();

    // ✅ Single department + its employees
    @EntityGraph(attributePaths = "employees")
    @Query("select d from Department d where d.id = :id")
    Optional<Department> findWithEmployeesById(@Param("id") Long id);
}
```

> **`distinct` enduku?** LEFT JOIN use chesthe duplicate rows vasthay. `distinct` vallanа unique departments mathrаme vasthay.

### EmployeeRepository.java

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // ✅ Employee + Department + Passport + Projects — ALL in ONE query!
    @EntityGraph(attributePaths = {"department", "passport", "projects"})
    @Query("select e from Employee e where e.id = :id")
    Optional<Employee> findDetailsById(@Param("id") Long id);
}
```

> **Multiple paths!** `attributePaths = {"department", "passport", "projects"}` — Hibernate 3 relationships ki 3 JOINs add chestundi. Result: **Single mega-query!**

### ProjectRepository.java

```java
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // ✅ All projects + their employees
    @EntityGraph(attributePaths = "employees")
    @Query("select distinct p from Project p")
    List<Project> findAllWithEmployees();

    // ✅ Single project + its employees
    @EntityGraph(attributePaths = "employees")
    @Query("select p from Project p where p.id = :id")
    Optional<Project> findWithEmployeesById(@Param("id") Long id);
}
```

---

## 🔍 SQL Comparison

### ❌ Without @EntityGraph

```sql
-- findAll() call chesthe:
SELECT * FROM departments;                            -- Query 1
SELECT * FROM employees WHERE department_id = 1;      -- Query 2
SELECT * FROM employees WHERE department_id = 2;      -- Query 3
SELECT * FROM employees WHERE department_id = 3;      -- Query 4
-- ... N more queries! 😱
```

### ✅ With @EntityGraph (findAllWithEmployees)

```sql
-- Single query! Motham data okesari vastundi!
SELECT DISTINCT d.*, e.*
FROM departments d
LEFT JOIN employees e ON d.id = e.department_id;      -- Just 1 Query! 🚀
```

### ✅ With @EntityGraph (findDetailsById — Employee)

```sql
-- Employee + ALL related data in ONE shot!
SELECT e.*, d.*, p.*, pr.*
FROM employees e
LEFT JOIN departments d ON e.department_id = d.id
LEFT JOIN passports p ON p.employee_id = e.id
LEFT JOIN employee_projects ep ON ep.employee_id = e.id
LEFT JOIN projects pr ON pr.id = ep.project_id
WHERE e.id = 1;                                        -- 1 MEGA Query! 💪
```

---

## 🧠 @EntityGraph = LAZY Override

Important concept: `@EntityGraph` Entity lo unna LAZY settings ni **override** chestundi.

```text
Entity Level (Default):                With @EntityGraph:
┌─────────────────────────┐            ┌─────────────────────────┐
│ Employee                │            │ findDetailsById()       │
│  department: LAZY  ❌   │   ──→      │  department: EAGER  ✅  │
│  passport: EAGER   ✅   │   ──→      │  passport: EAGER   ✅  │
│  projects: LAZY    ❌   │   ──→      │  projects: EAGER   ✅  │
└─────────────────────────┘            └─────────────────────────┘
```

> **Best Practice:** Entity lo anni LAZY unchali (safety). Specific queries lo `@EntityGraph` tho required data ni EAGER ga fetch cheyyali.

---

## 📊 Performance Numbers

| Method | Without @EntityGraph | With @EntityGraph |
|---|---|---|
| 5 Departments | **6 queries** | **1 query** |
| 100 Departments | **101 queries** | **1 query** |
| 1000 Departments | **1001 queries** | **1 query** |
| Employee with 3 relations | **4 queries** | **1 query** |

---

## 🎯 Key Takeaways

1. **N+1 Problem** = 1 query to get N items + N queries to get each item's relations = N+1 total
2. **@EntityGraph** = Solution — Hibernate ki "join chesi okesari teesko" ani instruction
3. **attributePaths** lo specify chesina fields EAGER ga fetch avthayi
4. **`distinct`** mandatory — JOINs vallа duplicate results raakundaa
5. **Entity lo LAZY, Query lo EAGER** — best practice combination
6. **findById vs findDetailsById** — findById uses entity defaults, findDetailsById uses @EntityGraph
7. **Production lo @EntityGraph lekapothe** → Database meedа daadi → Slow app → Users angry 😤

---

**Next Note:** [09_DTOs_Mapper_And_Controller_Layer.md](./09_DTOs_Mapper_And_Controller_Layer.md) — API layer explained 🔗

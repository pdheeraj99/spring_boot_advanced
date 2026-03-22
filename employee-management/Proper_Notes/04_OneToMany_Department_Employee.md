# 🔗 04. One-to-Many Relationship (Department ↔ Employee)

Mawa, One-to-Many ante **okka Department lo chala mandi Employees** untaru. Idi most common relationship — almost every real project lo untundi.

---

## 🎯 Core Concept

- **Okka Department** → **Chala Employees** (One-to-Many)
- **Okka Employee** → **Okka Department** ke belong avtadu (Many-to-One)
- Database lo Foreign Key **Employee table** lo untundi (`department_id`)
- **Golden Rule:** FK eppudu **"Many" side** lo untundi!

---

## 📝 Complete Code: Employee.java (Owner Side — FK Holder)

Employee **Owner** endukante — `department_id` column Employee table lo untundi.

```java
// Employee.java lo Department-related part:

@ManyToOne(fetch = FetchType.LAZY)         // ① Employee ki okka Department — LAZY loading
@JoinColumn(name = "department_id")        // ② employees table lo department_id column create avtundi
private Department department;
```

### Line-by-Line Explanation

| Annotation | Meaning |
|---|---|
| `@ManyToOne` | "Chala Employees okka Department ki belong avtaru" |
| `fetch = FetchType.LAZY` | Employee load chesthe Department **raadu**. `getDepartment()` call chesthene vastundi |
| `@JoinColumn(name = "department_id")` | `employees` table lo `department_id` column create avtundi — **Idi FK!** |

---

## 📝 Complete Code: Department.java (Inverse Side)

Department **Inverse** endukante — `mappedBy` use chestundi. Table lo extra column raadu.

```java
package com.employeemanagement.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "departments")
@Getter @Setter @NoArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String location;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    // ✅ Helper Method: Add Employee
    public void addEmployee(final Employee employee) {
        employees.add(employee);          // List lo add chestundi
        employee.setDepartment(this);     // Employee ki department set chestundi (Bidirectional sync!)
    }

    // ✅ Helper Method: Remove Employee
    public void removeEmployee(final Employee employee) {
        employees.remove(employee);       // List nundi remove chestundi
        employee.setDepartment(null);     // Employee nundi department link teestundi
    }
}
```

### Annotation Breakdown

| Annotation | Meaning |
|---|---|
| `mappedBy = "department"` | "FK naa daggara ledu. Employee class lo `department` field chudu, akkade definition undi" |
| `cascade = CascadeType.ALL` | Department save chesthe, andulo unna Employees anni automatic ga save avthayi |
| `orphanRemoval = true` | Department nundi employee remove chesthe, aa employee DB nundi delete avtundi |
| `new ArrayList<>()` | Empty list initialize — NullPointerException raakundaa |

---

## 🧠 Helper Methods — Enduku Kavali?

### ❌ Without Helper Method (Problem)

```java
Department dept = new Department();
Employee emp = new Employee();

dept.getEmployees().add(emp);  // Department → Employee ✅
// BUT emp.getDepartment() == null! ❌
// Database lo department_id will be NULL! 💥
```

### ✅ With Helper Method (Solution)

```java
Department dept = new Department();
Employee emp = new Employee();

dept.addEmployee(emp);
// Department → Employee ✅ (list lo add ayyadu)
// Employee → Department ✅ (internal ga setDepartment(this) call aindi)
// Database lo department_id will be SET! ✅
```

> **Key Point:** `addEmployee()` anedi rendu sides ni sync chestundi. `employee.setDepartment(this)` vallane database lo FK correctly save avtundi!

---

## 🗄️ Database Table View

```text
Table: DEPARTMENTS                 Table: EMPLOYEES (Owner — FK ikkade)
+----+--------+------------+       +----+-------+---------------------+---------------+
| ID | NAME   | LOCATION   |       | ID | NAME  | EMAIL               | DEPARTMENT_ID |
+----+--------+------------+       +----+-------+---------------------+---------------+
| 10 | IT     | Hyderabad  |  ←──  | 1  | Ram   | ram@example.com     | 10            |
| 20 | HR     | Bangalore  |  ←──  | 2  | Sita  | sita@example.com    | 10            |
+----+--------+------------+       | 3  | Ravi  | ravi@example.com    | 20            |
                                   +----+-------+---------------------+---------------+
                                                                         ↑
                                                                 Foreign Key (department_id)
```

> **Gamaninchu:**
>
> - Department table lo Employee list ekkada **ledu** — clean ga undi
> - Employee table lo `DEPARTMENT_ID` column vachindi — **Idi Foreign Key!**
> - Ram, Sita iddaru `dept_id = 10` (IT dept) lo unnaru

---

## 🔍 SQL Queries (Behind the Scenes)

### Department nundi Employees fetch

```sql
-- Department (Inverse) nundi Employees (Owner) ni testhe:
-- Department table lo employee info ledu, so Hibernate employees table ki veltundi
SELECT * FROM employees WHERE department_id = 10;  -- IT dept employees
```

### Employee nundi Department fetch

```sql
-- Employee (Owner) nundi Department (Inverse) ni testhe:
-- Employee lo department_id undi — direct ga vellipothundi!
SELECT * FROM departments WHERE id = 10;  -- direct lookup by FK value
```

---

## ⚠️ Lazy Loading Behavior

### Employee → Department (Owner side, LAZY explicitly set)

```text
employeeRepo.findById(1);
↓
Employee Object:
| id: 1, name: "Ram"                    |
| department: [ PROXY / HibernateProxy ] | ← RAALEDU! Empty dummy object
|                                        |
employee.getDepartment().getName();  ← Ippude DB call trigger avtundi!
```

### Department → Employees (Inverse side, Default LAZY)

```text
departmentRepo.findById(10);
↓
Department Object:
| id: 10, name: "IT"                     |
| employees: [ PersistentBag / Empty ]    | ← List RAALEDU! Proxy undi
|                                         |
dept.getEmployees().size();  ← Ippude DB call trigger ayyi employees load avthayi!
```

> **Default Rules:**
>
> - `@ManyToOne` default = **EAGER** (kani manam LAZY pettam ✅)
> - `@OneToMany` default = **LAZY** (safe default — list peddaga undachu kabatti)

---

## 🎯 Key Takeaways

1. **Owner = Employee** (FK holder, `@JoinColumn`) → database lo `department_id` column
2. **Inverse = Department** (`mappedBy`) → table lo extra column raadu
3. **FK eppudu "Many" side lo** — Employee table lo department_id. Department table lo employee IDs pettalem
4. **Helper methods** (`addEmployee`, `removeEmployee`) — bidirectional sync ki must!
5. **CascadeType.ALL** — Department save chesthe employees kooda save avthayi
6. **orphanRemoval** — Department nundi remove aina employee delete avtundi
7. **LAZY loading** — Employee load chesthe Department raadu, `getDepartment()` antene vastundi

---

**Next Note:** [05_ManyToMany_Employee_Project.md](./05_ManyToMany_Employee_Project.md) — Many-to-Many deep-dive 🔗

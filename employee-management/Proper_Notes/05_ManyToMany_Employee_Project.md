# 🔗 05. Many-to-Many Relationship (Employee ↔ Project)

Mawa, Many-to-Many ante — **okka Employee chala Projects chestadu, okka Project lo chala Employees untaru**. Idi OneToOne leda OneToMany laga simple direct link kaadu. Ikkada **Bridge Table (Join Table)** kavali!

---

## 🎯 Core Concept

- **Okka Employee** → **Multiple Projects** cheyagaladu
- **Okka Project** → **Multiple Employees** untaru
- **Problem:** Employee table lo `project_id` pedithe okka project eh possible. Project table lo `employee_id` pedithe okka employee eh possible. **Rendu wrong!**
- **Solution:** Iddari madhya oka **third table (join table)** → `employee_projects`

---

## 📝 Complete Code: Project.java (Owner Side)

Project **Owner** endukante — `@JoinTable` ikkade undi. Idi aa middle table ni manage chestundi.

```java
package com.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter @Setter @NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private LocalDate deadline;

    @ManyToMany                                                         // ① Many Projects ↔ Many Employees
    @JoinTable(
            name = "employee_projects",                                 // ② Join table name
            joinColumns = @JoinColumn(name = "project_id"),             // ③ Ee table (Project) ki FK
            inverseJoinColumns = @JoinColumn(name = "employee_id")      // ④ Aa table (Employee) ki FK
    )
    private Set<Employee> employees = new HashSet<>();

    // ✅ Helper: Add Employee to Project
    public void addEmployee(Employee employee) {
        this.employees.add(employee);       // Project lo employee add
        employee.getProjects().add(this);   // Employee lo project add (sync!)
    }

    // ✅ Helper: Remove Employee from Project
    public void removeEmployee(Employee employee) {
        this.employees.remove(employee);       // Project nundi employee remove
        employee.getProjects().remove(this);   // Employee nundi project remove (sync!)
    }
}
```

### @JoinTable Breakdown

| Part | Meaning |
|---|---|
| `name = "employee_projects"` | Database lo create ayye **middle table** name |
| `joinColumns = @JoinColumn(name = "project_id")` | Middle table lo **current entity (Project)** ki FK column |
| `inverseJoinColumns = @JoinColumn(name = "employee_id")` | Middle table lo **opposite entity (Employee)** ki FK column |

> **Simple ga:** `@JoinTable` = "Oka kotha table create chey, andulo naa ID and vaadi ID columns pettu"

---

## 📝 Complete Code: Employee.java (Inverse Side — Project part)

Employee **Inverse** endukante — `mappedBy` use chestundi. Join table ni manage cheyyadu.

```java
// Employee.java lo Project-related part:

@ManyToMany(mappedBy = "employees")           // ① "Project class lo employees field chudu"
private Set<Project> projects = new HashSet<>();

// ✅ Helper: Add Project to Employee
public void addProject(Project project) {
    this.projects.add(project);              // Employee lo project add
    project.getEmployees().add(this);        // Project lo employee add (sync!)
}

// ✅ Helper: Remove Project from Employee
public void removeProject(Project project) {
    this.projects.remove(project);           // Employee nundi project remove
    project.getEmployees().remove(this);     // Project nundi employee remove (sync!)
}
```

> **`mappedBy = "employees"`** = "Join table create chese responsibility naaadi kaadu, Project class lo `employees` field ne manage chestundi"

---

## 🧠 Why Set and Not List?

```java
private Set<Employee> employees = new HashSet<>();  // ← SET, not List!
```

**Reasons:**

1. **Duplicates prevent:** Same employee iddisarlu same project lo add avvakundaa Set prevent chestundi
2. **Performance:** Hibernate internally `Set` tho ManyToMany ni better ga handle chestundi
3. **Hibernate Recommendation:** ManyToMany ki `Set` vadamani official ga recommend chestaru

---

## 🗄️ Database Table View

```text
Table: PROJECTS              Table: EMPLOYEE_PROJECTS         Table: EMPLOYEES
+----+--------+------------+ (Bridge/Join Table)              +----+-------+
| ID | NAME   | DEADLINE   | +------------+-------------+    | ID | NAME  |
+----+--------+------------+ | PROJECT_ID | EMPLOYEE_ID |    +----+-------+
| 50 | Java   | 2026-06-15 | +------------+-------------+    | 1  | Ram   |
| 60 | React  | 2026-08-20 | | 50         | 1           |←──→| 2  | Sita  |
+----+--------+------------+ | 50         | 2           |    +----+-------+
                              | 60         | 1           |
                              +------------+-------------+
                                    ↑              ↑
                              FK to Projects  FK to Employees
```

> **Gamaninchu:**
>
> - Projects table lo Employee info **ledu**
> - Employees table lo Project info **ledu**
> - Link motham **EMPLOYEE_PROJECTS** (middle table) lo undi
> - Ram → Java AND React (2 projects)
> - Java → Ram AND Sita (2 employees)

---

## 🔍 SQL Queries (Behind the Scenes)

### Project nundi Employees fetch (Owner Side)

```sql
-- "Java Project (ID: 50) lo evarunnaru?"
-- Step 1: Middle table check chey
-- Step 2: Employee data teesko
SELECT e.*
FROM employee_projects ep
JOIN employees e ON e.id = ep.employee_id
WHERE ep.project_id = 50;
-- Result: Ram, Sita
```

### Employee nundi Projects fetch (Inverse Side)

```sql
-- "Ram (ID: 1) ye projects chestunnadu?"
-- Same — Middle table daggarke veltundi!
SELECT p.*
FROM employee_projects ep
JOIN projects p ON p.id = ep.project_id
WHERE ep.employee_id = 1;
-- Result: Java, React
```

> **Key Insight:** Etu side nundi fetch chesina, **middle table** ni always consult avvalsinde! Idi **WhatsApp Group** lanti concept — members list server lo untundi, not your phone or group lo.

---

## ⚠️ Lazy Loading Behavior

```text
employeeRepo.findById(1);
↓
Employee Object:
| id: 1, name: "Ram"                    |
| projects: [ PersistentSet / Empty ]    | ← RAALEDU! Proxy undi
|                                        |

employee.getProjects().size();  ← Ippude DB call trigger!
```

> **Default:** `@ManyToMany` default fetch = **LAZY**. List peddaga undachu kabatti safety kosam LAZY.

---

## 🎯 Key Takeaways

1. **Many-to-Many** ki **middle table** (join table) mandatory — direct FK pettalem
2. **Owner = Project** (`@JoinTable`) → middle table ni manage chestundi
3. **Inverse = Employee** (`mappedBy`) → middle table gurinchi baadhyata ledu
4. **Helper Methods** — rendu sides sync cheyyadam mandatory (addEmployee, removeEmployee)
5. **Set use cheyali** — List kaadu (duplicates prevent, Hibernate recommendation)
6. **SQL always middle table through** — etu side nundi fetch chesina bridge table check avtundi
7. **Default LAZY** — projects access chesinappude load avthayi

---

**Next Note:** [06_Owner_Vs_Inverse_And_MappedBy.md](./06_Owner_Vs_Inverse_And_MappedBy.md) — Owner vs Inverse consolidated 🔗

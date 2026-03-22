# 🏗️ 09. DTOs, Mapper & Controller Layer

Mawa, Entities ni direct ga API response lo pampadam **dangerous**! Anduke manam DTOs (Data Transfer Objects) vadutam. Ee note lo mana DTOs, Mapper, and Controllers motham explain chestanu.

---

## ❓ Enduku DTOs Kavali? (Entities Direct ga Pampithe?)

| Problem | Explanation |
|---|---|
| **Infinite Recursion** 💥 | Employee → Department → Employee List → Each Employee → Department → ... Jackson serialize chesthu infinite loop lo padatadu |
| **Security** 🔒 | Internal fields (passwords, FK IDs) expose avvakudadu |
| **Control** 🎯 | API lo exactly emi pampaalo control cheyochu |
| **Performance** ⚡ | Unnecessary data serialize avvadhu |

---

## 📝 All DTOs (Java Records)

Mana project lo **7 DTOs** unnayi, anni **Java Records** ga rasam (immutable, clean, boilerplate-free!).

### 1. EmployeeSummaryDto — Basic employee info

```java
package com.employeemanagement.dto;

public record EmployeeSummaryDto(Long id, String name, String email) {
}
```

> **Usage:** Department or Project response lo employees list chupinchinappudu — full details vaddu, just summary.

### 2. PassportDto — Passport details

```java
package com.employeemanagement.dto;

public record PassportDto(Long id, String passportNumber, String issuedCountry) {
}
```

> **Usage:** Employee detail response lo passport info chupinchinappudu.

### 3. ProjectSummaryDto — Basic project info

```java
package com.employeemanagement.dto;

import java.time.LocalDate;

public record ProjectSummaryDto(Long id, String name, LocalDate deadline) {
}
```

> **Usage:** Employee detail response lo projects list chupinchinappudu — employee details vaddu.

### 4. EmployeeDetailDto — Full employee details

```java
package com.employeemanagement.dto;

import java.util.List;

public record EmployeeDetailDto(
        Long id,
        String name,
        String email,
        String departmentName,       // Department object kaadu, just name string!
        PassportDto passport,        // PassportDto — clean object
        List<ProjectSummaryDto> projects  // Project summaries list
) {
}
```

> **Usage:** Single employee GET request ki response. Department object kaakunda just name string pampistam — clean and flat!

### 5. DepartmentDto — Department with employees

```java
package com.employeemanagement.dto;

import java.util.List;

public record DepartmentDto(Long id, String name, String location, List<EmployeeSummaryDto> employees) {
}
```

> **Usage:** Department GET response. Employees list EmployeeSummary ga untundi — not full details.

### 6. ProjectDto — Project with employees

```java
package com.employeemanagement.dto;

import java.time.LocalDate;
import java.util.List;

public record ProjectDto(Long id, String name, LocalDate deadline, List<EmployeeSummaryDto> employees) {
}
```

> **Usage:** Project GET response. Employees list summary ga.

### 7. MessageDto — Simple messages

```java
package com.employeemanagement.dto;

public record MessageDto(String message) {
}
```

> **Usage:** "Passport added successfully!" lanti simple messages kosam.

---

## 🔄 EntityToDtoMapper — Conversion Logic

Oka utility class lo Entity → DTO conversion methods anni pettam. **Private constructor** tho (instance create cheyyalem — all static methods).

```java
package com.employeemanagement.mapper;

import com.employeemanagement.dto.*;
import com.employeemanagement.entity.*;
import java.util.Collections;
import java.util.List;

public final class EntityToDtoMapper {

    private EntityToDtoMapper() {
    }  // ← Instance create cheyyalem!

    // Employee → EmployeeSummaryDto
    public static EmployeeSummaryDto toEmployeeSummary(Employee employee) {
        return new EmployeeSummaryDto(employee.getId(), employee.getName(), employee.getEmail());
    }

    // Department → DepartmentDto (with employee summaries)
    public static DepartmentDto toDepartmentDto(Department department) {
        List<EmployeeSummaryDto> employees = department.getEmployees() == null
                ? Collections.emptyList()
                : department.getEmployees().stream()
                    .map(EntityToDtoMapper::toEmployeeSummary)
                    .toList();

        return new DepartmentDto(department.getId(), department.getName(),
                                 department.getLocation(), employees);
    }

    // Passport → PassportDto (null-safe)
    public static PassportDto toPassportDto(Passport passport) {
        if (passport == null) {
            return null;  // Employee ki passport lekapothe null return
        }
        return new PassportDto(passport.getId(), passport.getPassportNumber(),
                               passport.getIssuedCountry());
    }

    // Project → ProjectSummaryDto
    public static ProjectSummaryDto toProjectSummary(Project project) {
        return new ProjectSummaryDto(project.getId(), project.getName(), project.getDeadline());
    }

    // Project → ProjectDto (with employee summaries)
    public static ProjectDto toProjectDto(Project project) {
        List<EmployeeSummaryDto> employees = project.getEmployees() == null
                ? Collections.emptyList()
                : project.getEmployees().stream()
                    .map(EntityToDtoMapper::toEmployeeSummary)
                    .toList();

        return new ProjectDto(project.getId(), project.getName(),
                              project.getDeadline(), employees);
    }

    // Employee → EmployeeDetailDto (full details — dept name + passport + projects)
    public static EmployeeDetailDto toEmployeeDetailDto(Employee employee) {
        String departmentName = employee.getDepartment() != null
                ? employee.getDepartment().getName() : null;
        PassportDto passport = toPassportDto(employee.getPassport());
        List<ProjectSummaryDto> projects = employee.getProjects() == null
                ? Collections.emptyList()
                : employee.getProjects().stream()
                    .map(EntityToDtoMapper::toProjectSummary)
                    .toList();

        return new EmployeeDetailDto(
                employee.getId(), employee.getName(), employee.getEmail(),
                departmentName, passport, projects
        );
    }
}
```

### Key Points

- **`final class` + private constructor** = Utility class pattern (no instances)
- **`null` checks everywhere** = Safe ga work chestundi
- **Streams** use chesi lists ni convert chestunnamu (`map + toList`)
- **Method references** = `EntityToDtoMapper::toEmployeeSummary` (clean syntax)

---

## 🌐 Controllers (All 3)

### 1. EmployeeController.java

```java
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    // POST /api/employees — Create employee
    @PostMapping
    public ResponseEntity<EmployeeDetailDto> createEmployee(@RequestBody Employee employee) {
        Employee saved = employeeRepository.save(employee);
        Employee withDetails = employeeRepository.findDetailsById(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Employee not found with id: " + saved.getId()));
        return ResponseEntity.ok(EntityToDtoMapper.toEmployeeDetailDto(withDetails));
    }

    // POST /api/employees/{id}/passport — Add passport to employee
    @PostMapping("/{id}/passport")
    public ResponseEntity<MessageDto> addPassport(@PathVariable Long id,
                                                   @RequestBody Passport passport) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Employee not found with id: " + id));

        employee.setPassport(passport);       // Smart setter! (Both sides sync)
        employeeRepository.save(employee);    // Cascade saves passport too!

        return ResponseEntity.ok(new MessageDto("Passport added successfully via Cascade!"));
    }

    // GET /api/employees/{id} — Get employee with all details
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDetailDto> getEmployee(@PathVariable Long id) {
        Employee employee = employeeRepository.findDetailsById(id)  // @EntityGraph!
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Employee not found with id: " + id));

        return ResponseEntity.ok(EntityToDtoMapper.toEmployeeDetailDto(employee));
    }

    // GET /api/employees/{id}/projects — Get employee's projects
    @GetMapping("/{id}/projects")
    public ResponseEntity<List<ProjectSummaryDto>> getEmployeeProjects(@PathVariable Long id) {
        Employee employee = employeeRepository.findDetailsById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Employee not found with id: " + id));

        List<ProjectSummaryDto> projects = employee.getProjects().stream()
                .map(EntityToDtoMapper::toProjectSummary)
                .toList();

        return ResponseEntity.ok(projects);
    }
}
```

### 2. DepartmentController.java

```java
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    // POST /api/departments — Create department
    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody Department department) {
        Department saved = departmentRepository.save(department);
        Department withEmployees = departmentRepository.findWithEmployeesById(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Department not found"));
        return ResponseEntity.ok(EntityToDtoMapper.toDepartmentDto(withEmployees));
    }

    // POST /api/departments/{deptId}/employees — Add employee to department
    @PostMapping("/{deptId}/employees")
    public ResponseEntity<DepartmentDto> addEmployee(@PathVariable Long deptId,
                                                      @RequestBody Employee employee) {
        Department department = departmentRepository.findWithEmployeesById(deptId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Department not found with id: " + deptId));

        department.addEmployee(employee);         // Helper method! (Both sides sync)
        departmentRepository.save(department);    // Cascade saves employee too!

        Department updated = departmentRepository.findWithEmployeesById(deptId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Department not found with id: " + deptId));

        return ResponseEntity.ok(EntityToDtoMapper.toDepartmentDto(updated));
    }

    // GET /api/departments — Get all departments with employees
    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departments = departmentRepository.findAllWithEmployees().stream()
                .map(EntityToDtoMapper::toDepartmentDto)
                .toList();
        return ResponseEntity.ok(departments);
    }
}
```

### 3. ProjectController.java

```java
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    // POST /api/projects — Create project
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody Project project) {
        Project saved = projectRepository.save(project);
        Project withEmployees = projectRepository.findWithEmployeesById(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Project not found with id: " + saved.getId()));
        return ResponseEntity.ok(EntityToDtoMapper.toProjectDto(withEmployees));
    }

    // GET /api/projects — Get all projects with employees
    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<ProjectDto> projects = projectRepository.findAllWithEmployees().stream()
                .map(EntityToDtoMapper::toProjectDto)
                .toList();
        return ResponseEntity.ok(projects);
    }

    // GET /api/projects/{id} — Get single project
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long id) {
        Project project = projectRepository.findWithEmployeesById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Project not found with id: " + id));
        return ResponseEntity.ok(EntityToDtoMapper.toProjectDto(project));
    }

    // POST /api/projects/{projectId}/employees/{employeeId} — Link employee to project
    @PostMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<MessageDto> linkEmployee(@PathVariable Long projectId,
                                                    @PathVariable Long employeeId) {
        Project project = projectRepository.findWithEmployeesById(projectId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Project not found with id: " + projectId));
        Employee employee = employeeRepository.findDetailsById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Employee not found with id: " + employeeId));

        boolean alreadyLinked = project.getEmployees().stream()
                .anyMatch(e -> e.getId().equals(employeeId));
        if (!alreadyLinked) {
            project.addEmployee(employee);       // Helper method!
            projectRepository.save(project);
            return ResponseEntity.ok(new MessageDto("Employee linked to project successfully"));
        }

        return ResponseEntity.ok(new MessageDto("Employee is already linked to this project"));
    }

    // DELETE /api/projects/{projectId}/employees/{employeeId} — Unlink employee
    @DeleteMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<MessageDto> unlinkEmployee(@PathVariable Long projectId,
                                                      @PathVariable Long employeeId) {
        Project project = projectRepository.findWithEmployeesById(projectId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Project not found with id: " + projectId));
        Employee employee = employeeRepository.findDetailsById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Employee not found with id: " + employeeId));

        boolean linked = project.getEmployees().stream()
                .anyMatch(e -> e.getId().equals(employeeId));
        if (linked) {
            project.removeEmployee(employee);     // Helper method!
            projectRepository.save(project);
            return ResponseEntity.ok(new MessageDto("Employee unlinked from project successfully"));
        }

        return ResponseEntity.ok(new MessageDto("Employee is not linked to this project"));
    }
}
```

---

## 🔄 Data Flow Visualization

```text
Client (Postman/Browser)
    │
    │  HTTP Request (JSON)
    ▼
┌──────────────────┐
│   Controller     │  ← Receives request, calls Repository
├──────────────────┤
│   Repository     │  ← Fetches Entity from DB (@EntityGraph!)
├──────────────────┤
│   Mapper         │  ← Entity → DTO conversion
├──────────────────┤
│   DTO            │  ← Clean response object
└──────────────────┘
    │
    │  HTTP Response (JSON)
    ▼
Client (Postman/Browser)
```

---

## 🎯 Key Takeaways

1. **DTOs** prevent infinite recursion, control data exposure, and improve security
2. **Java Records** — perfect for DTOs (immutable, no boilerplate, clean)
3. **EntityToDtoMapper** — centralized conversion, null-safe, uses method references
4. **Controllers** always use `@EntityGraph` methods (`findDetailsById`, `findWithEmployeesById`)
5. **Helper methods** used in controllers—`addEmployee()`, `setPassport()`—for bidirectional sync
6. **Cascade** via save — `departmentRepo.save(dept)` saves the new employee too!
7. **`@RequiredArgsConstructor`** — Lombok generates constructor for `final` fields (DI)

---

**Next Note:** [10_API_Endpoints_And_Testing_Guide.md](./10_API_Endpoints_And_Testing_Guide.md) — Complete API reference 🔗

# 📋 01. Project Overview & Setup

Mawa, ee project oka **Employee Management System**. Idi Spring Boot + JPA (Hibernate) + MySQL use chesi build chesam. Idi JPA Relationships (OneToOne, OneToMany, ManyToMany) ni **real-world** example tho practice cheyadaniki perfect project.

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| **Spring Boot 3.x** | Main framework — application ni run chestundi |
| **Spring Data JPA** | Database tho interact cheyadaniki (Repositories, Queries) |
| **Hibernate** | JPA implementation — SQL queries generate chestundi |
| **MySQL** | Database — tables ikkada create avthayi |
| **Lombok** | Boilerplate code reduce (Getters, Setters, Constructors) |
| **Java Records** | DTOs kosam lightweight immutable classes |

---

## 📁 Package Structure

```text
src/main/java/com/employeemanagement/
├── EmployeeManagementApplication.java    ← Main class (App start point)
│
├── entity/                               ← Database tables ki map ayye classes
│   ├── Employee.java                     ← Core entity (JPA relationships anni ikkade)
│   ├── Department.java                   ← Employee ki parent (One-to-Many)
│   ├── Passport.java                     ← Employee ki One-to-One
│   └── Project.java                      ← Employee tho Many-to-Many
│
├── repository/                           ← Database CRUD operations
│   ├── EmployeeRepository.java           ← @EntityGraph tho smart queries
│   ├── DepartmentRepository.java         ← Department + Employees fetch
│   ├── PassportRepository.java           ← Basic CRUD
│   └── ProjectRepository.java            ← Project + Employees fetch
│
├── dto/                                  ← API response kosam lightweight objects
│   ├── EmployeeDetailDto.java            ← Full employee details (dept + passport + projects)
│   ├── EmployeeSummaryDto.java           ← Just id, name, email
│   ├── DepartmentDto.java                ← Department + employee list
│   ├── PassportDto.java                  ← Passport details
│   ├── ProjectDto.java                   ← Project + employee list
│   ├── ProjectSummaryDto.java            ← Just id, name, deadline
│   └── MessageDto.java                   ← Simple success/error messages
│
├── mapper/                               ← Entity → DTO conversion
│   └── EntityToDtoMapper.java            ← All mapping methods ikkade
│
└── controller/                           ← REST API endpoints
    ├── EmployeeController.java           ← /api/employees
    ├── DepartmentController.java         ← /api/departments
    └── ProjectController.java            ← /api/projects
```

---

## ⚙️ Application Properties (Line-by-Line Explanation)

```properties
spring.application.name=employee-management
```

> App name — logs lo kanipistundi.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_management?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

> **MySQL connection** — `createDatabaseIfNotExist=true` pedithe database lekapothe automatic ga create chestundi. Manual ga DB create cheyyakkarladu!

```properties
spring.jpa.hibernate.ddl-auto=update
```

> **Idhi SUPER important!** `update` ante — mana Entity classes lo em changes chesina (new column, new table), Hibernate automatic ga database ni update chestundi. Production lo idi vadoddu, development lo super useful.

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

> Console lo Hibernate generate chesina **SQL queries** print avuthayi. `format_sql=true` ante readable format lo indentation tho vasthundi. Debugging ki bagunttundi!

```properties
server.port=8082
```

> App port 8082 lo run avtundi. Default 8080 already evaraina vaduthunte conflict raakundaa ila change chestam.

---

## 🚀 Main Application Class

```java
package com.employeemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmployeeManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }
}
```

> `@SpringBootApplication` — Idi 3 annotations ni combine chestundi:
>
> - `@Configuration` — Bean definitions
> - `@EnableAutoConfiguration` — Spring Boot auto config
> - `@ComponentScan` — Ee package + sub-packages lo beans vetukuntundi

---

## 🗄️ Database Tables (Auto-Created by Hibernate)

App run ayyaka MySQL lo ee tables automatic ga create avthayi:

```text
employee_management (Database)
├── employees            ← Employee entity nundi
├── departments          ← Department entity nundi
├── passports            ← Passport entity nundi
├── projects             ← Project entity nundi
└── employee_projects    ← Many-to-Many join table (auto-created by @JoinTable)
```

**Total: 5 tables** — 4 entity tables + 1 join table! 🎯

---

## 📌 Key Points to Remember

1. **Lombok** valla `@Getter`, `@Setter`, `@NoArgsConstructor` use chestunnamu — so getters/setters manual ga raayakkarladu
2. **Java Records** use chestunnamu DTOs kosam — immutable and clean
3. **`ddl-auto=update`** development kosam — production lo `validate` or `none` vadali
4. **`show-sql=true`** debugging kosam — Hibernate em queries run chestundo chudalanukuntene ON cheyali

---

**Next Note:** [02_Entity_Design_And_Relationships_Overview.md](./02_Entity_Design_And_Relationships_Overview.md) — Entities & Relationships overview 🔗

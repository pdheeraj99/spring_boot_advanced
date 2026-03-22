# 🔗 03. One-to-One Relationship (Employee ↔ Passport)

Mawa, One-to-One relationship ante **okka Employee ki okka Passport** mathrame untundi. Ee note lo mana actual code tho complete ga dissect cheddham.

---

## 🎯 Core Concept

- **Okka Employee** → **Okka Passport** mathrame
- **Okka Passport** → **Okka Employee** ki mathrame belong avtundi
- Database lo Foreign Key **Passport table** lo untundi (`employee_id`)

---

## 📝 Complete Code: Passport.java (Owner Side)

Passport **Owner** endukante — database lo Foreign Key (`employee_id`) idi hold chestundi.

```java
package com.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "passports")
@Getter @Setter @NoArgsConstructor
public class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "passport_number", nullable = false, unique = true)
    private String passportNumber;

    @Column(name = "issued_country")
    private String issuedCountry;

    @OneToOne(fetch = FetchType.LAZY)                          // ① LAZY — Employee data ventane raadu
    @JoinColumn(name = "employee_id", nullable = false, unique = true)  // ② FK ikkade untundi!
    private Employee employee;
}
```

### Line-by-Line Explanation

| Annotation | Meaning |
|---|---|
| `@OneToOne(fetch = FetchType.LAZY)` | Passport load chesinappudu Employee **raadu**. `getEmployee()` call chesthene vastadu |
| `@JoinColumn(name = "employee_id")` | Database lo `passports` table lo `employee_id` column create avtundi. **Idi FK!** |
| `nullable = false` | Passport ki Employee tappanisari undali (orphan passport allowed kaadu) |
| `unique = true` | Okka employee ki okka passport mathrame (duplicate FK prevent) |

---

## 📝 Complete Code: Employee.java (Inverse Side — Passport part)

Employee **Inverse** endukante — `mappedBy` use chestundi, database lo kotha column raadu.

```java
// Employee.java lo Passport-related part:

@OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
private Passport passport;
```

### Line-by-Line Explanation

| Annotation | Meaning |
|---|---|
| `mappedBy = "employee"` | "Naa daggara FK ledu. Passport class lo `employee` field lo definition undi, akkada chudu" |
| `cascade = CascadeType.ALL` | Employee ni save/delete chesthe, Passport kooda automatic ga save/delete avtundi |
| `orphanRemoval = true` | Employee nundi passport ni remove chesthe (null set chesthe), aa passport DB nundi delete avtundi |

---

## 🧠 Smart Setter (Helper Method) — Employee.java

Mawa, idi just normal setter kaadu. Idi **Bidirectional Sync** maintain chestundi!

```java
// Employee.java
public void setPassport(Passport passport) {
    // Step 1: Old passport link remove (Clean up)
    if (passport == null) {
        if (this.passport != null) {
            this.passport.setEmployee(null);  // Old passport nundi employee link teesey
        }
    } 
    // Step 2: New passport link set
    else {
        passport.setEmployee(this);  // New passport ki ee employee set chey
    }
    this.passport = passport;  // Employee ki passport assign chey
}
```

### Enduku ee Smart Setter kavali?

**Problem without it:**

```java
// ❌ Wrong way — only one side set avtundi
employee.setPassport(passport);  // Employee → Passport ✅
// But Passport → Employee is still NULL! ❌
```

**Solution with Smart Setter:**

```java
// ✅ Right way — rendu sides automatic ga sync
employee.setPassport(passport);
// Employee → Passport ✅
// Passport → Employee ✅ (Smart setter internally set chestundi)
```

---

## 🗄️ Database Table View

```text
Table: EMPLOYEES                 Table: PASSPORTS (Owner — FK ikkade)
+----+-------+--------+         +-----+-----------------+----------+-------------+
| ID | NAME  | EMAIL  |         | ID  | PASSPORT_NUMBER | COUNTRY  | EMPLOYEE_ID |
+----+-------+--------+         +-----+-----------------+----------+-------------+
| 1  | Ram   | r@e.c  |    ←──  | 100 | P12345          | INDIA    | 1           |
| 2  | Sita  | s@e.c  |    ←──  | 101 | P67890          | USA      | 2           |
+----+-------+--------+         +-----+-----------------+----------+-------------+
                                                            ↑
                                                    Foreign Key (employee_id)
```

> **Gamaninchu:** Employee table lo Passport gurinchi **em ledu**. Link motham Passport table lo `employee_id` column lo undi.

---

## 🔍 SQL Queries (Behind the Scenes)

### Employee nundi Passport fetch chesinappudu

```sql
-- Employee (Inverse) nundi Passport (Owner) ni testhe:
-- Employee table lo passport info ledu, so Hibernate passports table ki veltundi
SELECT * FROM passports WHERE employee_id = 1;
```

### Passport nundi Employee fetch chesinappudu

```sql
-- Passport (Owner) nundi Employee (Inverse) ni testhe:
-- Passport lo employee_id undi, so direct ga vellipothundi
SELECT * FROM employees WHERE id = 1;  -- (employee_id value use chesi)
```

---

## ⚠️ Lazy Loading Behavior

### Passport → Employee (Owner side, LAZY set chesam)

```text
passportRepo.findById(100);
↓
Passport Object (Memory)
+-------------------------+
| id: 100                 |
| passport_number: P12345 |
| employee: [ PROXY ] ----→ (Empty! DB call RAALEDU)
+-------------------------+

passport.getEmployee().getName();  ← Ippudu DB call veltundi! (Lazy trigger)
```

### Employee → Passport (Inverse side, Default EAGER)

```text
employeeRepo.findById(1);
↓
Employee Object (Memory)
+------------------------+
| id: 1                  |
| name: "Ram"            |
| passport: [LOADED] ----→ { id:100, number:"P12345" }  ← Already vachindi!
+------------------------+

employee.getPassport();  ← Extra DB call VELLADU! Already memory lo undi.
```

> **Enduku?** `@OneToOne` default fetch type = **EAGER**. Passport lo manam explicitly `LAZY` pettam, but Employee side lo pettaledu, so default EAGER apply avtundi.

---

## 🎯 Key Takeaways

1. **Owner = Passport** (FK holder) → `@JoinColumn` use chestundi
2. **Inverse = Employee** → `mappedBy` use chestundi, table lo extra column raadu
3. **Smart Setter** — bidirectional sync kosam tappanisari
4. **CascadeType.ALL** — Employee save chesthe Passport automatic ga save avtundi
5. **orphanRemoval** — Employee nundi passport remove chesthe, DB nundi delete avtundi
6. **Default Fetch:** Employee → Passport = EAGER | Passport → Employee = LAZY (explicit)

---

**Next Note:** [04_OneToMany_Department_Employee.md](./04_OneToMany_Department_Employee.md) — One-to-Many deep-dive 🔗

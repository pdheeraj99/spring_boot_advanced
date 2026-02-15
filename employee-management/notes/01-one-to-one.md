# OneToOne Relationship — Complete Notes

## 🎯 Scenario: Employee ↔ Passport

- Oka Employee ki **oka** Passport matrame untundi
- Oka Passport **oka** Employee ki matrame belong avuthundi

---

## 1. FK (Foreign Key) Ekkada Pettali?

### Rule: **Dependent/child entity lo FK pettali**

| Entity | Role | FK undha? |
|--------|------|-----------|
| Employee | **Primary** (independent ga exist avvachu) | ❌ FK ledu |
| Passport | **Dependent** (employee lekunda meaningless) | ✅ `employee_id` FK |

### Real-life logic

- Mana app lo **Employee = Boss**, **Passport = Assistant**
- Boss table lo FK raadu, Assistant table lo FK untundi
- Employee create chesthe passport lekunda undachu — but passport create cheyyalante employee kavali

---

## 2. Owning Side vs Inverse Side

```
Passport.java (OWNING SIDE)          Employee.java (INVERSE SIDE)
─────────────────────────             ─────────────────────────────
@OneToOne                             @OneToOne(mappedBy = "employee")
@JoinColumn(name = "employee_id")     private Passport passport;
private Employee employee;
                                      
FK NAA TABLE LO UNDI!                 FK NAA TABLE LO LEDU!
```

- **Owning side** = `@JoinColumn` untundi, DB lo FK column idi create chestundi
- **Inverse side** = `mappedBy` untundi, "nenu mirror ni, FK naa table lo raadu"

---

## 3. mappedBy Enduku Kavali? (TESTED & VERIFIED ✅)

### Without mappedBy — 2 FKs create avuthayi

```
employees table:  id, name, email, passport_id (FK)   ← EXTRA, WASTE!
passports table:  id, passport_number, employee_id (FK)
```

### With mappedBy = "employee" — Only 1 FK (correct!)

```
employees table:  id, name, email                      ← CLEAN!
passports table:  id, passport_number, employee_id (FK) ← ONLY HERE
```

### Why 2 FKs BAD?

- **Data inconsistency** — oka side update chesthe rendu match avvali
- **Extra storage** waste
- **"Two remotes for one TV"** — confusion!
- `mappedBy` cheptundi: "FK already Passport lo undi, duplicate create cheyaku!"

**Idi manamu MySQL Workbench lo live ga verify chesam!** 🔥

---

## 4. cascade = ALL — Enti Chestundi?

```java
@OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
private Passport passport;
```

### Without cascade

```java
Employee ravi = new Employee();
Passport passport = new Passport();
ravi.setPassport(passport);

employeeRepository.save(ravi);     // ❌ Passport save AVVADU!
passportRepository.save(passport); // Separately call cheyyali 😩
```

### With cascade = ALL

```java
employeeRepository.save(ravi);     // ✅ Passport kuda AUTO save avuthundi!
```

**cascade = ALL ante:** Parent (Employee) ni save/delete chesthe, child (Passport) kuda automatic ga save/delete avuthundi.

---

## 5. orphanRemoval = true — Enti Chestundi?

```java
employee.setPassport(null);          // Passport detach chesam
employeeRepository.save(employee);

// orphanRemoval = true  → DB lo passport row DELETE avuthundi ✅
// orphanRemoval = false → DB lo passport row ALAAGE untundi (orphan) 😬
```

**orphan = owner leni child.** `orphanRemoval = true` ante vadilipettina children ni clean up chey.

---

## 6. Helper Method — Enduku Kavali?

### Problem: Java lo objects BOTH sides sync avvali

```java
// WITHOUT helper method:
ravi.passport = passport;           // Employee side: ✅
// passport.employee = NULL          ← NOT SET! ❌

employeeRepository.save(ravi);
// SQL: INSERT INTO passports (..., employee_id) VALUES (..., NULL)
//                                                       ^^^^ ERROR!
```

### Solution: Helper method BOTH sides automatic ga sync chestundi

```java
public void setPassport(Passport passport) {
    if (passport == null) {
        // Remove: old passport lo employee null chey
        if (this.passport != null) {
            this.passport.setEmployee(null);
        }
    } else {
        // Add: new passport ki this employee set chey
        passport.setEmployee(this);  // ← BOTH SIDES SYNC!
    }
    this.passport = passport;
}
```

### How save works (step by step)

```
1. ravi.setPassport(passport)
   → Memory lo: ravi.passport = passport ✅
   → Memory lo: passport.employee = ravi ✅ (helper method valla)
   → DB lo: NOTHING happens yet!

2. employeeRepository.save(ravi)
   → Hibernate: "Employee save cheyyali..."
   → Hibernate: "cascade = ALL undi, passport kuda save cheyyali..."
   → Hibernate: "passport.employee = ravi, so employee_id = 1"
   → SQL: INSERT INTO employees (name, email) VALUES ('Ravi', 'ravi@test.com')
   → SQL: INSERT INTO passports (passport_number, employee_id) VALUES ('P123', 1)
```

| What | Who | When |
|------|-----|------|
| Java objects sync (memory) | `setPassport()` helper | Immediately |
| DB lo save | `cascade = ALL` | When `save()` called |

---

## 7. Generated SQL — Tables

```sql
-- Hibernate creates:
CREATE TABLE employees (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE passports (
    id BIGINT NOT NULL AUTO_INCREMENT,
    passport_number VARCHAR(255) NOT NULL UNIQUE,
    issued_country VARCHAR(255),
    employee_id BIGINT NOT NULL UNIQUE,
    PRIMARY KEY (id),
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);
```

---

## 8. Quick Reference

| Annotation | Where | Purpose |
|-----------|-------|---------|
| `@OneToOne` | Both sides | "Oka entity ki oka entity" |
| `@JoinColumn` | Owning side (Passport) | DB lo FK column create chestundi |
| `mappedBy` | Inverse side (Employee) | "FK already other side lo undi, duplicate cheyaku" |
| `cascade = ALL` | Parent side | Save/delete parent → child kuda auto save/delete |
| `orphanRemoval` | Parent side | Detach chesthe child DB lo delete |
| `fetch = LAZY` | Owning side | Data kaavalsinavappudu matrame load |

# OneToOne "Under the Hood" — SQL Magic Explained 🎩✨

Mawa, here is exactly what happens inside the Database when you write Java code. No magic, just logic!

## 1. Magic of `save()` (Creation)

**Java Code:**

```java
Employee ravi = new Employee();          // New Object (Memory)
Passport p = new Passport();             // New Object (Memory)

// 1. Sync in Java Memory
ravi.setPassport(p);                     // Link them (p.employee = ravi)

// 2. Save Parent (Employee)
employeeRepo.save(ravi);                 // 🔥 HIBERNATE STARTS HERE
```

**SQL Execution (Under the Hood):**
Hibernate sees `cascade = ALL`, so it knows it must save BOTH.

1. **Insert Parent first** (to generate ID):

    ```sql
    INSERT INTO employees (name, email) VALUES ('Ravi', 'ravi@test.com');
    -- Result: Generates Employee ID = 1
    ```

2. **Insert Child second** (using Parent's ID):

    ```sql
    INSERT INTO passports (passport_number, employee_id) VALUES ('P123', 1);
    -- Result: Uses Employee ID=1 as FK. Perfect link!
    -- This happens because 'Passport' is the Owning Side (mappedBy="employee" on other side told Hibernate this).
    ```

---

## 2. Magic of `get()` (Fetching)

### Scenario A: Fetching Employee (Inverse Side)

**Java:** `employeeRepo.findById(1)`

**Problem:** Employee table looks like this: `| 1 | Ravi |`
It has **NO Foreign Key**. Hibernate doesn't know if Ravi has a passport or not!

**Hibernate's Solution:** "I must check the `passports` table immediately!"

**SQL:**

```sql
SELECT 
    e.id, e.name,              -- Employee Data
    p.id, p.passport_number    -- Passport Data (fetched eagerly!)
FROM employees e
LEFT JOIN passports p ON p.employee_id = e.id  -- 👈 The JOIN!
WHERE e.id = 1;
```

**Why Magic?** You asked for Employee, but Hibernate smart-ly fetched Passport too in **ONE query** using `LEFT JOIN`.

---

### Scenario B: Fetching Passport (Owning Side)

**Java:** `passportRepo.findById(5)`

**Problem:** Passport table looks like this: `| 5 | P123 | 1 |` (FK=1)
Hibernate sees `employee_id = 1`.

**Hibernate's Solution:** "I see the FK is 1. I don't need to fetch Employee details yet. I'll just create a placeholder (Proxy)."

**SQL (Query 1):**

```sql
SELECT id, passport_number, employee_id FROM passports WHERE id = 5;
```

**Result:** It gets `employee_id = 1` but **DOES NOT JOIN** with Employee table. (Lazy Loading).

**Java:** `passport.getEmployee().getName()` (Touch the proxy)

**SQL (Query 2 - Fired NOW):**

```sql
SELECT id, name, email FROM employees WHERE id = 1;
```

**Why Magic?** Hibernate saved query time initially. It only ran the second query when you actually needed the name.

---

## 3. Magic of `orphanRemoval` (Deletion)

**Java:**

```java
employee.setPassport(null);      // Cut the link in memory
employeeRepo.save(employee);     // Update DB
```

**SQL Execution:**

1. Hibernate checks: "Wait, the link between Employee 1 and Passport 5 is broken."
2. It sees `orphanRemoval = true`.
3. **Result:** "I must kill the orphan!" 🗡️

```sql
DELETE FROM passports WHERE id = 5;
```

Without `orphanRemoval`, it would just set `employee_id = NULL` (or fail if NOT NULL). With it, it **DELETES the row**. Creates space!

---

### Summary Table

| Java Action | SQL Action | Why? |
|---|---|---|
| `save(Employee)` | `INSERT Employees` + `INSERT Passports` | `cascade=ALL` propagates the save. |
| `get(Employee)` | `SELECT ... LEFT JOIN Passports` | Inverse side has no FK, so it MUST check other table immediately. |
| `get(Passport)` | `SELECT Passports` (No join) | Owning side has FK, enables Lazy Loading. |
| `setPassport(null)` | `DELETE FROM Passports` | `orphanRemoval=true` cleans up disconnected data. |

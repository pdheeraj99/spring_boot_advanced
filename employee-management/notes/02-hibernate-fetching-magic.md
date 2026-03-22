# How Hibernate Knows to JOIN (Even if Data is Unknown) 🕵️‍♂️

Mawa, excellent question! You are thinking like a database engine now.

## The Secret: Metadata at Startup 🚀

Hibernate doesn't wait until you run a query to figure things out. It reads your Java classes **when the application starts**.

### 1. Startup Phase (Reading the Map)

When you run `SpringApplication.run()`, Hibernate scans your entities:

- It sees `Employee.class`:

  ```java
  @OneToOne(mappedBy = "employee")
  private Passport passport;
  ```

- **Hibernate thinks:**
  > *"Aha! `Employee` has a wife named `Passport`. Her address is in the `passports` table, column `employee_id`. Note to self: Whenever someone asks for Employee, I MUST check the `passports` table too!"*

It builds an internal "map" of your database structure in memory.

---

### 2. Runtime Phase (Executing the Query) 🏃‍♂️

Now, you call `employeeRepo.findById(1)`.

**What you think Hibernate does:**

1. Go to DB, find Employee 1.
2. Check if he has a passport.
3. If yes, fetch it.

**What Hibernate ACTUALLY does (The "Eager" Plan):**
Hibernate looks at its internal map:
> *"User wants Employee. My map says Employee has a `@OneToOne` with Passport. I don't know if THIS specific employee has one, but the RULE says I should fetch it just in case."*

So it generates a **SINGLE SMART QUERY**:

```sql
SELECT 
    e.*,    -- Give me all Employee columns
    p.*     -- AND give me all known Passport columns
FROM employees e
LEFT JOIN passports p ON p.employee_id = e.id  -- 👈 The Magic Link
WHERE e.id = 1;
```

**Why LEFT JOIN?**

- `LEFT JOIN` means: "Bring me the Employee. If a matching Passport exists, bring it too. If NOT, just bring NULL for passport columns."
- It covers **BOTH possibilities** (Has passport OR Doesn't have passport) in a **single shot**.

---

### 3. Conclusion

Hibernate doesn't "know" if data exists beforehand. It knows the **RELATIONSHIP Structure** (schema).
It writes a broad SQL query (`LEFT JOIN`) that can handle *any* data situation gracefully.

It's like ordering a "Combo Meal" 🍔🍟:

- You don't ask "Do you have fries?" then "Do you have a burger?"
- You say "Give me the Combo". If they are out of fries, they tell you. If they have both, you get both. Eager loading is the "Combo Meal".

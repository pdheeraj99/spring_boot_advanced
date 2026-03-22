# Does "LEFT JOIN" Solve N+1? 🤔

**Yes and No.**

### 1. For Single Entity (`findById`) — ✅ YES

When fetching **ONE** Employee:

- **Default/Eager:** Hibernate does `LEFT JOIN`.
- **Outcome:** 1 Query. No N+1 problem here.

### 2. For Lists (`findAll`) — ❌ THE REAL "N+1" DANGER ZONE

This is where N+1 actually happens. Imagine you have 10 Employees.

**Scenario:** You want to print every Employee's Passport number.

#### A. The N+1 Problem (Lazy Loading Default for Lists)

If you just say `employeeRepo.findAll()`:

1. **Query 1:** `SELECT * FROM employees` (Returns 10 rows).
2. **Loop:** For each employee, you call `getPassport()`.
   - Employee 1 → `SELECT * FROM passports WHERE employee_id=1`
   - Employee 2 → `SELECT * FROM passports WHERE employee_id=2`
   - ...
   - Employee 10 → `SELECT * FROM passports WHERE employee_id=10`

**Total Queries:** 1 (List) + 10 (Details) = **11 Queries (N+1)**.
Performance disaster! 💥

#### B. The Solution (JOIN FETCH)

To solve this in lists, we must tell Hibernate: *"Join them NOW, don't wait!"*

We write a custom query (JPQL):

```java
@Query("SELECT e FROM Employee e JOIN FETCH e.passport")
List<Employee> findAllWithPassports();
```

**Outcome:**

- **1 Query:** `SELECT * FROM employees e LEFT JOIN passports p ...`
- Takes 10 rows + 10 passports in **ONE go**.
- **Solved!** 🚀

### Summary

- `LEFT JOIN` (Eager) on single entity **prevents** extra queries.
- `N+1` mostly bites you when fetching **LISTS** (`OneToMany` or `findAll`).
- We will see this LIVE in **Step 2 (Department → Employees)**!

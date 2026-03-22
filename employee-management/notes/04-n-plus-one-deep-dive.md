# Does "N+1" Exist in OneToOne? 🤔

**Short Answer:** YES, it CAN happen, but it's rare because of defaults.

### 1. OneToOne (EAGER Default) — Safe ✅

By default, `@OneToOne` uses `FetchType.EAGER`.

- When you load **List<Employee>**:
  - Hibernate knows it MUST fetch Passport for each employee immediately.
  - It usually does a optimize and uses **Targeted Batch Fetching** or `JOIN` if possible (though for `findAll()`, it might still separate them if not careful).
  - But generally, for single entities, it's safe.

### 2. OneToOne (LAZY) — Danger Zone ⚠️

If you change it to `@OneToOne(fetch = FetchType.LAZY)` (on the parent side without bytecode enhancement):

- **Problem:** Hibernate can't use a proxy for the inverse side (because it doesn't know if it's null).
- **Result:** It executes **N+1 queries** anyway to check for nullability!
  - 1 Query for List<Employee>
  - N Queries to check if each employee has a passport.
- **Fix:** Use `mappedBy` correctly + Bytecode Enhancement (Advanced) OR just stick to Eager/Join Fetch.

### 3. OneToMany (LAZY Default) — The Real Villain 🦹‍♂️

- A Department has **List<Employee>**.
- Default is `FetchType.LAZY`.
- You load List<Department>. (1 Query)
- You loop through them and call `.getEmployees()`.
- **BOOM!** N queries fire immediately.
- **Why more common here?** Because Collections (Lists/Sets) are always Lazy by default. You almost ALWAYS hit this problem with lists.

### Conclusion

- **OneToOne:** N+1 is possible but less frequent because we often fetch Eagerly or use Shared PKs.
- **OneToMany:** N+1 is the **#1 Performance Issue** because Collections are Lazy. You WILL face this.

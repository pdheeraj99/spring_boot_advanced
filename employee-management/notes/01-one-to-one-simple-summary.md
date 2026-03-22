# OneToOne: The "Golden Rules" (Keep it Simple!)

Don't worry about Eager/Lazy for now. Just remember these **3 Rules**:

## Rule 1: Who holds the Foreign Key (FK)?
> **"Dependent" entity holds the FK.**
- **Passport** depends on **Employee**.
- So `passports` table gets `employee_id`.

## Rule 2: How to map in Java?
- **Passport (Owning Side)**: Needs `@JoinColumn(name="employee_id")` because it has the FK.
- **Employee (Inverse Side)**: Needs `mappedBy="employee"` to say "I don't have the FK, look at Passport".

## Rule 3: How to save?
- Use `cascade = CascadeType.ALL` on Employee.
- Use a **Helper Method** (`setPassport`) to link them in Java.
- Just save **Employee**, and Passport gets saved automatically!

---

### That's it! 
If you remember these 3 rules, you can build any OneToOne relationship. 
The SQL and Fetch Types are "under the hood" magic — you don't need to memorize them to write the code!

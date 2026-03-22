# ⚖️ 06. Owner vs Inverse Side & mappedBy

Mawa, JPA lo **Owner** and **Inverse** concept idi most confusing topic. Kani actually chala simple — just oka Golden Rule gurthu pettukunte saripothundi!

---

## 🔔 The Golden Rule

> **"Foreign Key (FK) evari Table lo untundo, vaade OWNER!"**

- **Owner Side** → Database lo FK column hold chestundi → `@JoinColumn` use chestundi
- **Inverse Side** → Database lo em change cheyyadu → `mappedBy` use chesi Owner ni point chestundi

---

## 📊 Mana Project lo Owner vs Inverse

| Relationship | Owner (FK Holder) | Inverse (mappedBy) | FK / Join Table Location |
|---|---|---|---|
| Employee ↔ Passport | **Passport** | Employee | `passports.employee_id` |
| Department ↔ Employee | **Employee** | Department | `employees.department_id` |
| Employee ↔ Project | **Project** | Employee | `employee_projects` (join table) |

---

## 🔍 Code lo Ela Kanipistundi?

### 1. One-to-One (Passport = Owner, Employee = Inverse)

```java
// Passport.java (OWNER — @JoinColumn undi)
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "employee_id", nullable = false, unique = true)  // ← FK ikkade!
private Employee employee;

// Employee.java (INVERSE — mappedBy undi)
@OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
private Passport passport;  // ← Table lo extra column RAADU
```

**DB Result:** `passports` table lo `employee_id` column create avtundi. `employees` table lo passport gurinchi em ledu.

---

### 2. One-to-Many (Employee = Owner, Department = Inverse)

```java
// Employee.java (OWNER — @JoinColumn undi)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "department_id")  // ← FK ikkade!
private Department department;

// Department.java (INVERSE — mappedBy undi)
@OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Employee> employees = new ArrayList<>();  // ← Table lo extra column RAADU
```

**DB Result:** `employees` table lo `department_id` column create avtundi. `departments` table lo employee list ekkada ledu.

> **Logic:** Database lo List ni okka cell lo store cheyyalem (comma separated pettalem kadaa!). Anduke FK eppudu **"Many" side** (Employee) lo untundi.

---

### 3. Many-to-Many (Project = Owner, Employee = Inverse)

```java
// Project.java (OWNER — @JoinTable undi)
@ManyToMany
@JoinTable(
    name = "employee_projects",                             // ← Middle table!
    joinColumns = @JoinColumn(name = "project_id"),
    inverseJoinColumns = @JoinColumn(name = "employee_id")
)
private Set<Employee> employees = new HashSet<>();

// Employee.java (INVERSE — mappedBy undi)
@ManyToMany(mappedBy = "employees")
private Set<Project> projects = new HashSet<>();  // ← Middle table manage cheyyadu
```

**DB Result:** `employee_projects` ane **kotha table** create avtundi. `projects` and `employees` tables lo ekkada inkokari info ledu.

---

## ❓ mappedBy Exactly Em Chestundi?

`mappedBy` anedi Hibernate ki oka **instruction**:

```java
// Department.java
@OneToMany(mappedBy = "department")
```

**Meaning:** *"Orey Hibernate, naa daggara FK ledu. Employee class lo oka field undi — daani peru `department`. Akkada complete relationship definition undi. Nuvvu akkada chudu, FK management anthaa akkadnundi cheyyi."*

**Without mappedBy em avtundi?** 😱

- Hibernate ki teliadu evaru owner ani
- **Rendu sides ki separate FK columns** create chestundi
- Extra join table create avvachu (waste!)
- Data confusion and bugs

---

## ⚠️ Enduku Okare Owner Undali?

**Problem (if both are owners):**

```java
// ❌ Imagine rendu sides lo @JoinColumn pedithe:
// Passport lo employee_id ✅
// Employee lo passport_id ✅ (waste column!)
// = Circular dependency + duplicate data + confusion
```

**Solution (one owner, one mirror):**

```java
// ✅ Passport holds FK
// ✅ Employee just mirrors via mappedBy
// = Clean, single source of truth
```

---

## 💡 Cheat Code (Quick Trick)

Eppudaina doubt vasthe ee 2 questions vesko:

**Question 1: "Database lo FK column evari table lo undali?"**
→ Answer evaraithe, **vaade Owner**. Vaade `@JoinColumn` rayali.

**Question 2: "Inkoka class em cheyyali?"**
→ Vaallu `mappedBy` use chesi → *"Link akkada undi, naa daggara kaadu"* ani cheppali.

---

## 📝 Real-World Examples (Quick Reference)

| Relationship | FK Ekkada? | Owner | Why? |
|---|---|---|---|
| Employee - Department | `employees.dept_id` | Employee | Many side lo FK |
| Order - Customer | `orders.customer_id` | Order | Many side lo FK |
| Post - Comment | `comments.post_id` | Comment | Many side lo FK |
| Student - Course | `student_courses` (join table) | Depends on `@JoinTable` placement | ManyToMany ki join table |
| User - Profile | `profiles.user_id` | Profile | OneToOne — choice based |

---

## 🎯 Key Takeaways

1. **Owner = FK holder** → `@JoinColumn` (or `@JoinTable` for ManyToMany)
2. **Inverse = Mirror** → `mappedBy` use chesi Owner ni point chestundi
3. **Without mappedBy** → Hibernate confused ayyi extra tables/columns create chestundi
4. **OneToMany lo FK eppudu "Many" side lo** → Database lo list store cheyyalem kabatti
5. **ManyToMany lo join table** → Neither side lo FK pettalem, third table kavali
6. **Owner side nundi save chesthe saripothundi** — FK automatically set avtundi

---

**Next Note:** [07_Lazy_Vs_Eager_Loading.md](./07_Lazy_Vs_Eager_Loading.md) — Fetch strategies deep-dive 🔗

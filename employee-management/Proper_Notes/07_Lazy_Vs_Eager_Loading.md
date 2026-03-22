# ⚡ 07. Lazy vs Eager Loading

Mawa, Lazy vs Eager loading ante — **related data ni eppudu database nundi teesukuravaali?** ane decision. Idi performance ki **heart** lanti concept!

---

## 🎯 Core Concept

| Type | Meaning | Analogy |
|---|---|---|
| **EAGER** | Main entity load chesthe related data **ventane** vastundi | Amazon lo Phone order chesthe, Charger kooda **ventane** box lo vastundi 📦 |
| **LAZY** | Main entity load chesthe related data **raadu**. Access chesinappude vastundi | Phone vastundi. Charger kavali ante **separate request** pettali 📱 |

---

## 📊 Default Fetch Types (Gurthu Pettuko!)

| Relationship | Default | Logic (Enduku?) |
|---|---|---|
| **@OneToOne** | **EAGER** | Okka record eh kada, pedda load undadu |
| **@ManyToOne** | **EAGER** | Parent okkare, load cheseyochu |
| **@OneToMany** | **LAZY** | List lo 1000 records undachu — anni okesari testhe crash! ⚠️ |
| **@ManyToMany** | **LAZY** | Ikkada kooda list peddaga undachu |

> **Simple Rule:** Single items (ToOne) → EAGER. Collections (ToMany) → LAZY.

---

## 🔍 Mana Code lo Fetch Types

### A. Explicitly Set Chesindi (Manam Raasam)

```java
// Passport.java — Employee ni LAZY ga pettam
@OneToOne(fetch = FetchType.LAZY)    // Default EAGER ni override chesam!
@JoinColumn(name = "employee_id")
private Employee employee;

// Employee.java — Department ni LAZY ga pettam
@ManyToOne(fetch = FetchType.LAZY)   // Default EAGER ni override chesam!
@JoinColumn(name = "department_id")
private Department department;
```

### B. Implicitly Default (Manam Raayaledu)

```java
// Department.java — Employees list
@OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Employee> employees = new ArrayList<>();
// fetch rayaledu → Default: LAZY ✅ (Safe!)

// Employee.java — Projects set
@ManyToMany(mappedBy = "employees")
private Set<Project> projects = new HashSet<>();
// fetch rayaledu → Default: LAZY ✅ (Safe!)

// Employee.java — Passport
@OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
private Passport passport;
// fetch rayaledu → Default: EAGER ⚠️ (Passport always loads with Employee!)
```

---

## 📦 Employee Object — What Loads, What Doesn't?

Nuvvu `employeeRepository.findById(1)` call chesthe (NO `@EntityGraph`):

```text
Employee Object (Memory)
+------------------------------+
| ID: 1                        | ✅ Direct field — always vachindi
| Name: "Ram"                  | ✅ Direct field — always vachindi
| Email: "ram@example.com"     | ✅ Direct field — always vachindi
|                              |
| Passport:                    | ✅ VACHINDI! (Default EAGER — OneToOne)
|   { id:100, no:"P12345" }   |    employee.getPassport() → No DB call needed
|                              |
| Department:                  | ❌ RAALEDU! (Explicit LAZY — ManyToOne)
|   [ HibernateProxy ]        |    employee.getDepartment() → DB call trigger!
|                              |
| Projects:                    | ❌ RAALEDU! (Default LAZY — ManyToMany)
|   [ PersistentSet ]         |    employee.getProjects() → DB call trigger!
+------------------------------+
```

---

## 🔮 Proxy Object — Enti Idi?

LAZY loading chesthe, Hibernate **real object** return cheyyadu. Instead, oka **Proxy (Dummy Object)** return chestundi.

```text
employee.getDepartment() returns:
+-----------------------------------+
| HibernateProxy$$Department        |  ← Real Department kaadu!
|   id: 10  (just ID telusu)       |
|   name: ???  (data ledu)         |
|   location: ???  (data ledu)     |
+-----------------------------------+

employee.getDepartment().getName()  ← THIS triggers the actual DB query!
                                       Proxy "touch" aindi → Hibernate DB ki veltundi
```

> **Proxy Rules:**
>
> - `.getId()` → DB call **velladu** (ID already proxy lo untundi)
> - `.getName()` → DB call **veltundi** (real data fetch kavali)
> - `.toString()` → DB call **veltundi** (data access avtundi)

---

## ⚡ Lazy Loading Trigger — Prathi Scenario

### Scenario 1: Passport → Employee (LAZY set chesam)

```java
Passport passport = passportRepo.findById(100L).get();
// Query: SELECT * FROM passports WHERE id = 100;
// Employee: PROXY (raaledu)

String name = passport.getEmployee().getName();  // ← TRIGGER!
// Query: SELECT * FROM employees WHERE id = 1;
// Ippudu Employee data vachindi
```

### Scenario 2: Employee → Department (LAZY set chesam)

```java
Employee emp = employeeRepo.findById(1L).get();
// Query: SELECT e.*, p.* FROM employees e LEFT JOIN passports p ... WHERE e.id = 1;
// Department: PROXY (raaledu)
// Passport: LOADED (EAGER default)

String deptName = emp.getDepartment().getName();  // ← TRIGGER!
// Query: SELECT * FROM departments WHERE id = 10;
```

### Scenario 3: Employee → Projects (Default LAZY)

```java
Employee emp = employeeRepo.findById(1L).get();
// Projects: EMPTY SET (raaledu)

int count = emp.getProjects().size();  // ← TRIGGER!
// Query: SELECT p.* FROM employee_projects ep JOIN projects p ... WHERE ep.employee_id = 1;
```

---

## ⚠️ LazyInitializationException — Common Trap

```java
// ❌ WRONG — Transaction baitaki vasthe Proxy panicheyyadhu!
@GetMapping("/employee/{id}")
public String getDeptName(@PathVariable Long id) {
    Employee emp = employeeRepo.findById(id).get();  // Transaction close avtundi
    return emp.getDepartment().getName();  // 💥 LazyInitializationException!
    // Endukante: Transaction close ayyaka Proxy "dead" aipotundi
}
```

**Solution:** `@EntityGraph` use cheyali (Next note lo detailed ga!)

---

## 📊 Performance Comparison

| Scenario | EAGER | LAZY |
|---|---|---|
| **Queries** | 1 Query (JOIN) | 2+ Queries (Main + Trigger) |
| **Memory** | More data loaded upfront | Data on-demand |
| **Good When?** | Related data **always** needed | Related data **sometimes** needed |
| **Risk** | Unnecessary data loading → Slow startup | N+1 Problem (loop lo triggers) |

---

## 🎯 Key Takeaways

1. **Defaults:** Single (ToOne) = EAGER | Collections (ToMany) = LAZY
2. **Best Practice:** Explicitly LAZY pettali ToOne relationships ki kooda (`@ManyToOne(fetch = LAZY)`)
3. **Proxy:** LAZY field access chesthe Hibernate dummy object return chestundi — actual data kavali antene DB call
4. **Trigger:** Proxy meeda non-ID method (getName, toString) call chesthe DB query run avtundi
5. **LazyInitializationException:** Transaction baitaki vachi Proxy access chesthe crash! Solution = `@EntityGraph`
6. **Mana Code lo:** Department, Passport(from Employee side default EAGER), Projects — mostly LAZY

---

**Next Note:** [08_N1_Problem_And_EntityGraph.md](./08_N1_Problem_And_EntityGraph.md) — N+1 problem & solution 🔗

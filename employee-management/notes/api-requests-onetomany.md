# Postman API Collection — OneToMany (Department ↔ Employee)

Open Postman, create a new collection called "**Employee Management**", and add these requests:

---

## 1. Create Department (Parent)

**Method:** `POST`  
**URL:** `http://localhost:8081/api/departments`  
**Description:** Creates a new Department (e.g., Engineering).

**Body (JSON):**

```json
{
  "name": "Engineering",
  "location": "Building A"
}
```

---

## 2. Add Employee to Department (OneToMany)

**Method:** `POST`  
**URL:** `http://localhost:8081/api/departments/1/employees`  
**Description:** Adds an employee to Department ID 1. This uses `cascade` to save the employee.

**Body (JSON):**

```json
{
  "name": "Priya",
  "email": "priya@test.com"
}
```

---

## 3. Add Another Employee

**Method:** `POST`  
**URL:** `http://localhost:8081/api/departments/1/employees`  
**Description:** Adds a second employee to the SAME department.

**Body (JSON):**

```json
{
  "name": "Kiran",
  "email": "kiran@test.com"
}
```

---

## 4. Get All Departments (Verify List)

**Method:** `GET`  
**URL:** `http://localhost:8081/api/departments`  
**Description:** Retrieves all departments. You should see "Engineering" with a list containing "Priya" and "Kiran".

---

## 5. Verify Employee's Department (ManyToOne)

**Method:** `GET`  
**URL:** `http://localhost:8081/api/employees/1`  
**Description:** (Optional - if we updated EmployeeController) Checks if Employee 1 knows their department.

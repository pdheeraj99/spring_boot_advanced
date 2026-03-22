package com.employeemanagement.repository;

import com.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @EntityGraph(attributePaths = {"department", "passport", "projects"})
    @Query("select e from Employee e where e.id = :id")
    Optional<Employee> findDetailsById(@Param("id") Long id);
}

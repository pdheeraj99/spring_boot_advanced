package com.employeemanagement.repository;

import com.employeemanagement.entity.Department;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @EntityGraph(attributePaths = "employees")
    @Query("select distinct d from Department d")
    List<Department> findAllWithEmployees();

    @EntityGraph(attributePaths = "employees")
    @Query("select d from Department d where d.id = :id")
    Optional<Department> findWithEmployeesById(@Param("id") Long id);
}

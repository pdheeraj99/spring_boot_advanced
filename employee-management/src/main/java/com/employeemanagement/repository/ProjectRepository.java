package com.employeemanagement.repository;

import com.employeemanagement.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @EntityGraph(attributePaths = "employees")
    @Query("select distinct p from Project p")
    List<Project> findAllWithEmployees();

    @EntityGraph(attributePaths = "employees")
    @Query("select p from Project p where p.id = :id")
    Optional<Project> findWithEmployeesById(@Param("id") Long id);
}

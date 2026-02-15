package com.example.jpaacademy.onetomany.repo;

import com.example.jpaacademy.onetomany.entity.Mother;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MotherRepo extends JpaRepository<Mother, Long> {

    @EntityGraph(attributePaths = { "children" })
    List<Mother> findAllBy();

    @EntityGraph(attributePaths = { "children" })
    Optional<Mother> findWithChildrenById(Long id);
}

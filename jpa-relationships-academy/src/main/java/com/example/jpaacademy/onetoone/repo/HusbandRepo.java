package com.example.jpaacademy.onetoone.repo;

import com.example.jpaacademy.onetoone.entity.Husband;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HusbandRepo extends JpaRepository<Husband, Long> {

    /**
     * @EntityGraph → Wife ni EAGER ga load chestundi (select lo JOIN use chestundi)
     *              Lekapothey wife LAZY ga undi, transaction bayata access chesthe
     *              LazyInitializationException vastundi!
     */
    @EntityGraph(attributePaths = { "wife" })
    List<Husband> findAllBy();

    @EntityGraph(attributePaths = { "wife" })
    Optional<Husband> findWithWifeById(Long id);
}

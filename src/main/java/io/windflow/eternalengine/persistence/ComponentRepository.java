package io.windflow.eternalengine.persistence;

import io.windflow.eternalengine.entities.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ComponentRepository extends JpaRepository<Component, UUID> {

    @Modifying
    @Transactional
    @Query("delete from Component c")
    void truncate();

    Optional<Component> findByNamespaceAndComponentName(String namespace, String componentName);

}

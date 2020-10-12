package io.windflow.eternalengine.persistence;

import io.windflow.eternalengine.entities.Component;
import io.windflow.eternalengine.entities.ExtensionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExtensionDataRepository extends JpaRepository<ExtensionData, UUID> {

    @Modifying
    @Transactional
    @Query("delete from ExtensionData ed")
    void truncate();

    List<ExtensionData> findByFullyQualifiedClassName(String fullyQualifiedClassName);
}

package io.windflow.eternalengine.persistence;

import io.windflow.eternalengine.entities.DomainLookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface DomainLookupRepository extends JpaRepository<DomainLookup, UUID> {

    @Modifying
    @Transactional
    @Query("delete from DomainLookup d")
    void truncate();

    Set<DomainLookup> findByOwnerEmail(String ownerEmail);

    Optional<DomainLookup> findFirstByDomainAlias(String domainAlias);
}

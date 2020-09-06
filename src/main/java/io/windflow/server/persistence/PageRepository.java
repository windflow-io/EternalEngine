package io.windflow.server.persistence;

import io.windflow.server.entities.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PageRepository extends JpaRepository<Page, UUID> {

    Optional<Page> findByDomainAndPath(String domain, String path);

    Optional<Page> findByDomainAndType(String domain, Page.PageType type);

    @Query(value="select * from page where json ->> 'street' = :street", nativeQuery = true)
    List<Page> findByStreet(@Param("street") String street);

    Boolean existsByDomain(String domain);

    Boolean existsByType(Page.PageType type);

    Boolean existsBy();

    @Modifying
    @Transactional
    @Query("delete from Page p")
    void truncate();
}

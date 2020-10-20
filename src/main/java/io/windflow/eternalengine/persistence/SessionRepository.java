package io.windflow.eternalengine.persistence;

import io.windflow.eternalengine.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    Optional<Session> findById(UUID uuid);

    @Modifying
    void deleteByUserId(UUID uuid);
}

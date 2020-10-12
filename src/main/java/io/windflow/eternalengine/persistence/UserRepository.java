package io.windflow.eternalengine.persistence;

import io.windflow.eternalengine.entities.EternalEngineUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<EternalEngineUser, UUID> {

    Optional<EternalEngineUser> findByEmail(String email);

}

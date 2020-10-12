package io.windflow.eternalengine.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Session {

    @Id
    @GeneratedValue
    UUID id;

}

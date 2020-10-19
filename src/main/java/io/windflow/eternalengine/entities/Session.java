package io.windflow.eternalengine.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
public class Session {

    @Id
    @GeneratedValue
    UUID id;

    ZonedDateTime expires;
    String clientIp;
    UUID userId;

    public Session() {}

    public Session(UUID userId, String clientIp) {
        expires = ZonedDateTime.now().plusSeconds(30);
        this.userId = userId;
        this.clientIp = clientIp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ZonedDateTime getExpires() {
        return expires;
    }

    public void setExpires(ZonedDateTime expires) {
        this.expires = expires;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}

package io.windflow.server.entities;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Page {

    /* AuthenticationData */
    /* ComponentName */

    @Id
    @GeneratedValue
    UUID id;

    String domain;

    String path;

    String version;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    String json;

    LocalDate lastUpdated;

    LocalDate expires;

    /** Getters and Setters **/

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JsonRawValue
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public LocalDate getExpires() {
        return expires;
    }

    public void setExpires(LocalDate expires) {
        this.expires = expires;
    }

    /*** Methods ***/

    @PreUpdate @PrePersist
    private void setTheDate() {
        lastUpdated = LocalDate.now();
    }
}

package io.windflow.server.entities;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Page {

    /*@TODO: Authorization mechanism please */

    @Id
    @GeneratedValue
    UUID id;

    String domain;

    String path;

    @Enumerated(EnumType.STRING)
    PageType type;

    Float version = 0f;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    String json;

    LocalDateTime lastUpdated;

    public Page() {}

    public Page (String domain, String path, PageType type, String json) {
        this.domain = domain;
        this.path = path;
        this.type = type;
        this.json = json;
    }

    /*** Methods ***/

    @PreUpdate @PrePersist
    private void setTheDate() {
        lastUpdated = LocalDateTime.now();
    }

    /** Inner Classes **/

    public static enum PageType {
        PageNormal,
        Page404
    }

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

    public PageType getType() {
        return type;
    }

    public void setType(PageType type) {
        this.type = type;
    }

    public Float getVersion() {
        return version;
    }

    public void setVersion(Float version) {
        this.version = version;
    }

    @Lob @JsonRawValue
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }


}

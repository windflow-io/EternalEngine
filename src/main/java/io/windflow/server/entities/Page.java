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

    public PageType getType() {
        return type;
    }

    public void setType(PageType type) {
        this.type = type;
    }

    @Enumerated(EnumType.STRING)
    PageType type;

    Float version;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    String json;

    LocalDate lastUpdated;

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

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    /*** Methods ***/

    @PreUpdate @PrePersist
    private void setTheDate() {
        lastUpdated = LocalDate.now();
    }

    public static enum PageType {
        Page404, Page500, PageDefault, PageNormal, PageNoSite
    }
}

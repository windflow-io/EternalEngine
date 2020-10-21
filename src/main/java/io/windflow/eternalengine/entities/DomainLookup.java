package io.windflow.eternalengine.entities;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(indexes = {@Index(name="index_columns", columnList="siteId,domainAlias,ownerId", unique = true)})
public class DomainLookup {

    @Id
    @GeneratedValue
    UUID id;

    String siteId;
    String domainAlias;
    UUID ownerId;
    String herokuCanonicalName;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getDomainAlias() {
        return domainAlias;
    }

    public void setDomainAlias(String domainAlias) {
        this.domainAlias = domainAlias;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public String getHerokuCanonicalName() {
        return herokuCanonicalName;
    }

    public void setHerokuCanonicalName(String herokuCanonicalName) {
        this.herokuCanonicalName = herokuCanonicalName;
    }
}

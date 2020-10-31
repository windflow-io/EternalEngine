package io.windflow.eternalengine.entities;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(indexes = {@Index(name="index_domain_lookup", columnList="site_id,domain_alias,owner_email")})
public class DomainLookup {

    @Id
    @GeneratedValue
    UUID id;

    @Column(name="domain_alias")
    String domainAlias;

    @Column(name="site_id")
    String siteId;

    @Column(name="owner_email")
    String ownerEmail;

    String herokuCanonicalName;

    public DomainLookup() {}

    public DomainLookup(String domainAlias, String siteId, String ownerEmail) {
        this.siteId = siteId;
        this.domainAlias = domainAlias;
        this.ownerEmail = ownerEmail;
    }

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

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerId) {
        this.ownerEmail = ownerId;
    }

    public String getHerokuCanonicalName() {
        return herokuCanonicalName;
    }

    public void setHerokuCanonicalName(String herokuCanonicalName) {
        this.herokuCanonicalName = herokuCanonicalName;
    }

}

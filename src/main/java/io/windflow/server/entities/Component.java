package io.windflow.server.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Component {

    @Id
    @GeneratedValue
    UUID id;

    String namespace;
    String componentName;
    Float version;

    LocalDate lastUpdated;

    @Lob
    String javaScript;

    /*** Getters and Setters ***/

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Float getVersion() {
        return version;
    }

    public void setVersion(Float version) {
        this.version = version;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public String getJavaScript() {
        return javaScript;
    }

    public void setJavaScript(String javaScript) {
        this.javaScript = javaScript;
    }

    /*** Methods ***/

    @PreUpdate
    @PrePersist
    private void setTheDate() {
        lastUpdated = LocalDate.now();
    }

}

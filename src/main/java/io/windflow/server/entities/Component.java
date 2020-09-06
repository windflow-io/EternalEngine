package io.windflow.server.entities;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Component {

    @Id
    @GeneratedValue
    UUID id;

    String namespace;
    String componentName;
    Float version = 0f;

    @Type(type="org.hibernate.type.TextType")
    String javaScript;

    LocalDateTime lastUpdated;

    ComponentType componentType;

    public Component() {}

    public Component(String namespace, String componentName, ComponentType componentType, String javaScript) {
        this.namespace = namespace;
        this.componentName = componentName;
        this.javaScript = javaScript;
        this.componentType = componentType;
    }

    /*** Methods ***/

    @PreUpdate
    @PrePersist
    private void setTheDate() {
        lastUpdated = LocalDateTime.now();
    }

    /*** Inner Classes ***/

    public static enum ComponentType {
        LAYOUT, COMPONENT
    }


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

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getJavaScript() {
        return javaScript;
    }

    public void setJavaScript(String javaScript) {
        this.javaScript = javaScript;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }
}

package io.windflow.eternalengine.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.windflow.eternalengine.services.DomainFinder;
import io.windflow.eternalengine.utils.JsonStringifiable;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Component extends JsonStringifiable     {

    @Id
    @GeneratedValue
    UUID id;

    String namespace;

    String componentName;
    Float version = 0f;

    @Type(type="org.hibernate.type.TextType")
    String javascript;

    @Type(type="org.hibernate.type.TextType")
    String singleFileComponent;

    LocalDateTime lastUpdated;

    ComponentType componentType;

    public Component() {}

    public Component(String namespace, String componentName, ComponentType componentType, String javascript, String singleFileComponent) {
        this.namespace = namespace;
        this.componentName = componentName;
        this.javascript = javascript;
        this.singleFileComponent = singleFileComponent;
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

    @JsonIgnore
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @JsonIgnore
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @JsonProperty("name")
    public String getComponentName() {
        return componentName;
    }

    @JsonProperty("name")
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    @JsonIgnore
    public Float getVersion() {
        return version;
    }

    public void setVersion(Float version) {
        this.version = version;
    }

    @JsonIgnore
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    @JsonIgnore
    public String getJavascript() {
        return javascript;
    }

    public void setJavascript(String javaScript) {
        this.javascript = javaScript;
    }

    @JsonProperty("sfc")
    public String getSingleFileComponent() {
        return singleFileComponent;
    }

    @JsonProperty("sfc")
    public void setSingleFileComponent(String singleFileComponent) {
        this.singleFileComponent = singleFileComponent;
    }

    @JsonProperty("type")
    public ComponentType getComponentType() {
        return componentType;
    }

    @JsonProperty("type")
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    /** Additional Getters **/

    @JsonProperty ("id")
    public String getComponentId() {
        return namespace + "." + componentName;
    }

    @JsonProperty ("id")
    public void setComponentId(String componentId) {
        DomainFinder.NamespaceAndComponentName parts = DomainFinder.extractParts(componentId);
        this.componentName = parts.getComponentName();
        this.namespace = parts.getNamespace();
    }
}

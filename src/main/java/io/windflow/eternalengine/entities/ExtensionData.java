package io.windflow.eternalengine.entities;

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class ExtensionData {

    @Id
    @GeneratedValue
    UUID id;

    String fullyQualifiedClassName;
    String key;
    Boolean keyEncrypted;

    @Type(type="org.hibernate.type.TextType")
    String value;

    public ExtensionData() {
        ;;
    }

    public ExtensionData(String fullyQualifiedClassName, String key, String value, Boolean keyEncrypted) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
        this.key = key;
        this.value = value;
        this.keyEncrypted = keyEncrypted;
    }


    /*** Getters and Setters ***/

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullyQualifiedClassName() {
        return fullyQualifiedClassName;
    }

    public void setFullyQualifiedClassName(String className) {
        this.fullyQualifiedClassName = className;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean isKeyEncrypted() {
        return keyEncrypted;
    }

    public void setKeyEncrypted(Boolean keyEncrypted) {
        this.keyEncrypted = keyEncrypted;
    }
}

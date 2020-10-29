package io.windflow.eternalengine.beans.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.windflow.eternalengine.utils.JsonStringifiable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleFileComponent extends JsonStringifiable {

    private String name;
    private String sfc;
    private String code;

    public SingleFileComponent() {

    }

    public SingleFileComponent(String componentName, String sfcSource) {
        this.name = componentName;
        this.sfc = sfcSource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSfc() {
        return sfc;
    }

    public void setSfc(String sfc) {
        this.sfc = sfc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

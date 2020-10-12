package io.windflow.eternalengine.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.windflow.eternalengine.utils.JsonStringifiable;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PageData extends JsonStringifiable {

    MetaInfo metaInfo;
    String layout;
    HashSet<Area> areas = new HashSet();

    public class MetaInfo {
        String title;
        HtmlAttrs htmlAttrs;
        BodyAttrs bodyAttrs;
        Set<Meta> meta = new HashSet<>();
        Set<Link> link = new HashSet<>();

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public HtmlAttrs getHtmlAttrs() {
            return htmlAttrs;
        }

        public void setHtmlAttrs(HtmlAttrs htmlAttrs) {
            this.htmlAttrs = htmlAttrs;
        }

        public BodyAttrs getBodyAttrs() {
            return bodyAttrs;
        }

        public void setBodyAttrs(BodyAttrs bodyAttrs) {
            this.bodyAttrs = bodyAttrs;
        }

        public Set<Meta> getMeta() {
            return meta;
        }

        public void setMeta(Set<Meta> meta) {
            this.meta = meta;
        }

        public Set<Link> getLink() {
            return link;
        }

        public void setLink(Set<Link> link) {
            this.link = link;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HtmlAttrs {

        String lang;

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BodyAttrs {

        @JsonProperty("class")
        String cssClass;

        @JsonProperty("class")
        public String getCssClass() {
            return cssClass;
        }

        @JsonProperty("class")
        public void setCssClass(String cssClass) {
            this.cssClass = cssClass;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {

        String name;
        String charset;
        String content;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Link {

        String rel;
        String type;
        String href;
        String sizes;

        public String getRel() {
            return rel;
        }

        public void setRel(String rel) {
            this.rel = rel;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getSizes() {
            return sizes;
        }

        public void setSizes(String sizes) {
            this.sizes = sizes;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Area {

        String area;
        HashSet<Component> components = new HashSet<Component>();

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public HashSet<Component> getComponents() {
            return components;
        }

        public void setComponents(HashSet<Component> components) {
            this.components = components;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Component {

        String id;
        String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public HashSet<Area> getAreas() {
        return areas;
    }

    public void setAreas(HashSet<Area> areas) {
        this.areas = areas;
    }

    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(MetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }


}

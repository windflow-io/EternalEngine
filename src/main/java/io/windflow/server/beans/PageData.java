package io.windflow.server.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PageData {

    String title;
    String lang;
    String encoding;
    Integer httpStatus;
    MetaData metaData;
    String layout;
    HashSet<Area> areas = new HashSet();

    public class MetaData {

        Set<MetaTag> metaTags = new HashSet<>();
        Set<Link> links = new HashSet<>();

        public Set<MetaTag> getMetaTags() {
            return metaTags;
        }

        public void setMetaTags(Set<MetaTag> metaTags) {
            this.metaTags = metaTags;
        }

        public Set<Link> getLinks() {
            return links;
        }

        public void setLinks(Set<Link> links) {
            this.links = links;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetaTag {

        String name;
        String content;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setPageHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
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

    @Override
    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "Could not express object as JSON";
        }
    }
}

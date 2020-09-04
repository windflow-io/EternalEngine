package io.windflow.server.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PageData {

    MetaData metaData;

    public class MetaData {
        String title;
        String description;
        Integer httpStatus;
        Set<MetaTag> metaTags = new HashSet<>();
        Set<MetaTag> links = new HashSet<>();

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getHttpStatus() {
            return httpStatus;
        }

        public void setHttpStatus(Integer httpStatus) {
            this.httpStatus = httpStatus;
        }

        public Set<MetaTag> getMetaTags() {
            return metaTags;
        }

        public void setMetaTags(Set<MetaTag> metaTags) {
            this.metaTags = metaTags;
        }

        public Set<MetaTag> getLinks() {
            return links;
        }

        public void setLinks(Set<MetaTag> links) {
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
    public static class Links {

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

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "Could not express object as JSON";
        }
    }
}

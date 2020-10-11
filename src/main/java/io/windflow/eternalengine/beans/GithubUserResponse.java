package io.windflow.eternalengine.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.windflow.eternalengine.utils.JsonStringifiable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubUserResponse extends JsonStringifiable {

    String name;
    String email;
    String avatar_url;
    String location;
    String company;

    @JsonProperty("login")
    String githubUsername;

    @JsonProperty("blog")
    String githubHomepage;

    @JsonProperty("url")
    String githubUserData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public String getGithubHomepage() {
        return githubHomepage;
    }

    public void setGithubHomepage(String githubHomepage) {
        this.githubHomepage = githubHomepage;
    }

    public String getGithubUserData() {
        return githubUserData;
    }

    public void setGithubUserData(String githubUserData) {
        this.githubUserData = githubUserData;
    }
}

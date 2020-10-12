package io.windflow.eternalengine.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.windflow.eternalengine.utils.JsonStringifiable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubUser extends JsonStringifiable {

    String name;
    String email;

    @JsonProperty("avatar_url")
    String avatarUrl;
    String location;
    String company;

    @JsonProperty("login")
    String githubUsername;

    @JsonProperty("blog")
    String githubHomepage;

    @JsonProperty("url")
    String githubUserDataUrl;

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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public String getGithubUserDataUrl() {
        return githubUserDataUrl;
    }

    public void setGithubUserDataUrl(String githubUserDataUrl) {
        this.githubUserDataUrl = githubUserDataUrl;
    }
}

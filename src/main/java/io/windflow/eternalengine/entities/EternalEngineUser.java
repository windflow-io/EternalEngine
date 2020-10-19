package io.windflow.eternalengine.entities;

import io.windflow.eternalengine.beans.GithubUser;
import io.windflow.eternalengine.utils.JsonStringifiable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class EternalEngineUser extends JsonStringifiable {

    @Id
    @GeneratedValue
    UUID id;

    String name;
    String email;
    String avatarUrl;
    String location;
    String company;
    AuthenticationProvider authenticationProvider = AuthenticationProvider.GITHUB;
    String githubUsername;
    String githubHomepage;
    String githubUserDataUrl;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
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

    public void setGithubUserDataUrl(String githubUserData) {
        this.githubUserDataUrl = githubUserData;
    }

    public enum AuthenticationProvider {
        GITHUB
    }

    public static EternalEngineUser createFromGithubUser(GithubUser githubUser) {
        EternalEngineUser user = new EternalEngineUser();
        user.setName(githubUser.getName());
        user.setEmail(githubUser.getEmail());
        user.setCompany(githubUser.getCompany());
        user.setLocation(githubUser.getLocation());
        user.setAvatarUrl(githubUser.getAvatarUrl());
        user.setGithubHomepage(githubUser.getGithubHomepage());
        user.setGithubUserDataUrl(githubUser.getGithubUserDataUrl());
        user.setGithubUsername(githubUser.getGithubUsername());
        user.setAuthenticationProvider(AuthenticationProvider.GITHUB);
        return user;
    }

}

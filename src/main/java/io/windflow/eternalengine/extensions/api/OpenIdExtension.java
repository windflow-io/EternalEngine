package io.windflow.eternalengine.extensions.api;

import io.windflow.eternalengine.beans.GithubTokenResponse;
import io.windflow.eternalengine.extensions.framework.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class OpenIdExtension<T extends Plugin> extends Plugin implements Requestable, Respondable, Actionable, Datafiable {

    HttpServletRequest request;
    HttpServletResponse response;
    HashMap<String, String> keyValues = new HashMap<>();

    final String REDIRECT_TO_GITHUB = "github_redirect";
    final String GITHUB_CALLBACK = "github_callback";

    @Override
    public void injectRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void injectResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public String performAction(String actionName, Object data) throws ExtensionException {

        final String BASE_URL_AUTH = keyValues.get("github_base_url_auth"); if (BASE_URL_AUTH == null) throw new ExtensionException("github_base_url_auth value cannot be null");
        final String CALLBACK_URL = keyValues.get("github_callback_url"); if (CALLBACK_URL == null) throw new ExtensionException("github_callback_url value cannot be null");
        final String BASE_URL_TOKEN = keyValues.get("github_base_url_token"); if (BASE_URL_TOKEN == null) throw new ExtensionException("github_base_url_token value cannot be null");
        final String CLIENT_ID = keyValues.get("github_client_id"); if (CLIENT_ID == null) throw new ExtensionException("github_client_id value cannot be null");
        final String CLIENT_SECRET = keyValues.get("github_client_secret"); if (CLIENT_SECRET == null) throw new ExtensionException("github_client_secret value cannot be null");
        final String ALLOW_SIGNUP = keyValues.get("github_allow_signup"); if (ALLOW_SIGNUP == null) throw new ExtensionException("github_allow_signup value cannot be null");
        final String SCOPE = keyValues.get("github_scope"); if (SCOPE == null) throw new ExtensionException("github_scope value cannot be null");

        switch (actionName) {
            case REDIRECT_TO_GITHUB:

                String referer = request.getHeader("referer");
                if (referer == null) throw new ExtensionException("Header must contain a referer header. Direct browser visits not accepted.", null);

                String state = URLEncoder.encode(referer, StandardCharsets.UTF_8);
                final String authRedirectUrl = BASE_URL_AUTH + "?client_id=" + CLIENT_ID + "&scope=" + SCOPE + "&state=" + state + "&allow_signup=" + ALLOW_SIGNUP + "&redirect_uri=" + CALLBACK_URL;

                try {
                    response.sendRedirect(authRedirectUrl);
                } catch (IOException ex) {
                    throw new ExtensionException("Could not redirect browser.", ex);
                }
                break;

            case GITHUB_CALLBACK: {

                final String code = request.getParameter("code");
                final String returnedState = request.getParameter("state");
                final String tokenUrl = BASE_URL_TOKEN + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&code=" + code + "&redirect_uri=" + CALLBACK_URL;

                RestTemplate template = new RestTemplate();
                String token = template.getForObject(tokenUrl, GithubTokenResponse.class).getAccessToken();

                if (token == null) throw new ExtensionException("Github returned no token");


                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "token " + token);

                HttpEntity entity = new HttpEntity(headers);

                String userInfoUrl = "https://api.github.com/user";
                ResponseEntity<String> userInfoEntity = template.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);
                System.out.println("USER DATA");
                System.out.println(userInfoEntity.getBody());

                Cookie githubCookie = new Cookie("github_token", token);
                githubCookie.setPath("/");
                githubCookie.setMaxAge(604800); // 1 week
                response.addCookie(githubCookie);

                try {
                    response.sendRedirect(returnedState);
                } catch (IOException ex) {
                    throw new ExtensionException("Could not redirect browser.", ex);
                }

            }
            default:
                throw new ExtensionException("Action " + actionName + " was not recognised.");
        }
        throw new ExtensionException("Unexpected Error.");
    }

    @Override
    public void injectData(HashMap<String, String> keyValues) {
        this.keyValues = keyValues;
    }
}

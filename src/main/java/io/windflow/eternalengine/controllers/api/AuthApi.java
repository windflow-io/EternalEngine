package io.windflow.eternalengine.controllers.api;

import io.windflow.eternalengine.beans.GithubTokenResponse;
import io.windflow.eternalengine.beans.GithubUser;
import io.windflow.eternalengine.entities.User;
import io.windflow.eternalengine.error.WindflowError;
import io.windflow.eternalengine.error.WindflowWebException;

import io.windflow.eternalengine.persistence.UserRepository;
import io.windflow.eternalengine.utils.HttpError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@PropertySource({"classpath:secret.properties","classpath:openid.properties"})
public class AuthApi {

    @Value("${io.windflow.auth.github_client_id}")
    String GITHUB_CLIENT_ID;

    @Value("${io.windflow.auth.github_client_secret}")
    String GITHUB_CLIENT_SECRET;

    @Value("${io.windflow.auth.github_auth_domain}")
    String GITHUB_CALLBACK_DOMAIN;

    final String GITHUB_ALLOW_SIGNUP = "true";
    final String SCOPE = "read:user+user:email";

    private final String GITHUB_LOGIN_URL = "https://github.com/login/oauth/authorize";
    private final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private final String GITHUB_CALLBACK_URL = GITHUB_CALLBACK_DOMAIN + "/api/auth/github/callback";

    private UserRepository userRepository;

    public AuthApi(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/auth/github")
    public void redirectUserToGitHub(HttpServletRequest request, HttpServletResponse response) throws WindflowWebException {

        String referer = request.getHeader("referer");

        if (referer == null) throw new WindflowWebException(WindflowError.ERROR_008, "Auth refused to redirect use to github without referer header");

        String state = URLEncoder.encode(referer, StandardCharsets.UTF_8);
        final String authRedirectUrl = GITHUB_LOGIN_URL + "?client_id=" + GITHUB_CLIENT_ID + "&scope=" + SCOPE + "&state=" + state + "&allow_signup=" + GITHUB_ALLOW_SIGNUP + "&redirect_uri=" + GITHUB_CALLBACK_URL;

        try {
            response.sendRedirect(authRedirectUrl);
        } catch (IOException ex) {
            throw new WindflowWebException(WindflowError.ERROR_001, "Could not send redirect", ex);
        }
    }

    /**
     * Callback URL should be: [protocol://domain:port]/api/auth/github/callback
     * @param request
     * @throws WindflowWebException
     */
    @GetMapping("/api/auth/github/callback")
    public void receiveUserFromGitHub(HttpServletRequest request, HttpServletResponse response) throws WindflowWebException {

        if (request.getParameter("error") != null) {
            String errorString = request.getParameter("error") + ": " + request.getParameter("error_description");
            throw new WindflowWebException(WindflowError.ERROR_009, errorString);
        }

        final String code = request.getParameter("code");
        final String tokenUrl = GITHUB_TOKEN_URL + "?client_id=" + GITHUB_CLIENT_ID + "&client_secret=" + GITHUB_CLIENT_SECRET + "&code=" + code + "&redirect_uri=" + GITHUB_CALLBACK_URL;

        RestTemplate template = new RestTemplate();
        GithubTokenResponse token = template.getForObject(tokenUrl, GithubTokenResponse.class);
        if (token == null) throw new WindflowWebException(WindflowError.ERROR_009, "No response to token request");
        if (token.getError() != null) {
            throw new WindflowWebException(WindflowError.ERROR_009, token.getError() + ": " + token.getErrorDescription());
        }


        GithubUser githubUser = fetchUserData(token.getAccessToken());
        Optional<User> optUser = userRepository.findByEmail(githubUser.getEmail());
        User windflowUser;
        if (optUser.isPresent()) {
            windflowUser = optUser.get();
        } else {
            windflowUser = User.createFromGithubUser(githubUser);
            userRepository.save(windflowUser);
        }

        /* @TODO: CREATE A SESSION HERE AND RETURN A SESSION ID - THAT SESSION SHOULD INCLUDE IP, EXPIRY, ETC */

        try {
            response.sendRedirect(request.getParameter("state"));
        } catch (IOException ex) {
            throw new WindflowWebException(WindflowError.ERROR_001, "Could not send redirect", ex);
        }

    }

    private GithubUser fetchUserData(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + token);
        HttpEntity entity = new HttpEntity(headers);
        String userInfoUrl = "https://api.github.com/user";
        ResponseEntity<GithubUser> userInfoEntity = new RestTemplate().exchange(userInfoUrl, HttpMethod.GET, entity, GithubUser.class);
        return userInfoEntity.getBody();
    }

    @ExceptionHandler(WindflowWebException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpError handleWindflowWebException(WindflowWebException windEx) {
        windEx.printStackTrace();
        return new HttpError(HttpStatus.NOT_FOUND.value(), windEx.getWindflowError(), windEx.getDetailOnly());
    }

}

package io.windflow.eternalengine.controllers.api;

import io.windflow.eternalengine.beans.GithubTokenResponse;
import io.windflow.eternalengine.beans.GithubUser;
import io.windflow.eternalengine.beans.dto.Token;
import io.windflow.eternalengine.entities.EternalEngineUser;
import io.windflow.eternalengine.entities.Session;
import io.windflow.eternalengine.error.WindflowError;
import io.windflow.eternalengine.error.WindflowWebException;

import io.windflow.eternalengine.persistence.SessionRepository;
import io.windflow.eternalengine.persistence.UserRepository;
import io.windflow.eternalengine.beans.dto.HttpError;
import io.windflow.eternalengine.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@PropertySource("classpath:openid.properties")
public class AuthApi {

    @Value("${io.windflow.auth.github_client_id}")
    String GITHUB_CLIENT_ID;

    @Value("${io.windflow.auth.github_client_secret}")
    String GITHUB_CLIENT_SECRET;

    @Value("${io.windflow.auth.github_auth_domain}")
    String GITHUB_CALLBACK_DOMAIN;

    //@Value("${io.windflow.auth.cookiedomain}")
    //String GITHUB_COOKIE_DOMAIN;

    final String GITHUB_ALLOW_SIGNUP = "true";
    final String SCOPE = "read:user+user:email";

    private final String GITHUB_LOGIN_URL = "https://github.com/login/oauth/authorize";
    private final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";

    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private AuthService authService;

    public AuthApi(@Autowired UserRepository userRepository, @Autowired SessionRepository sessionRepository, @Autowired AuthService authService) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.authService = authService;
    }

    @GetMapping("/api/auth/github")
    public void redirectUserToGitHub(HttpServletRequest request, HttpServletResponse response) throws WindflowWebException {

        final String GITHUB_CALLBACK_URL = GITHUB_CALLBACK_DOMAIN + "/api/auth/github/callback";

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

        final String GITHUB_CALLBACK_URL = GITHUB_CALLBACK_DOMAIN + "/api/auth/github/callback";
        final String code = request.getParameter("code");
        final String tokenUrl = GITHUB_TOKEN_URL + "?client_id=" + GITHUB_CLIENT_ID + "&client_secret=" + GITHUB_CLIENT_SECRET + "&code=" + code + "&redirect_uri=" + GITHUB_CALLBACK_URL;

        checkForErrors(request);
        GithubTokenResponse token = fetchToken(tokenUrl);
        GithubUser githubUser = fetchUserData(token.getAccessToken());
        EternalEngineUser user = createOrFetchUser(githubUser);
        Session session = sessionRepository.save(new Session(user.getId(), request.getRemoteAddr()));

        try {
            response.addCookie(createCookie(session.getId()));
            response.sendRedirect(request.getParameter("state"));
        } catch (IOException ex) {
            throw new WindflowWebException(WindflowError.ERROR_001, "Could not send redirect", ex);
        }

    }

    @GetMapping("/api/auth/github/exchange/{exchangeToken}")
    @Transactional
    protected Token tokenExchange(HttpServletRequest request, HttpServletResponse response, @PathVariable("exchangeToken") String exchangeToken) {
        String sessionId = new String (Base64.getDecoder().decode(exchangeToken));
        EternalEngineUser user = authService.exchangeToken(sessionId, request.getRemoteAddr());
        return authService.createJWT(user);
    }

    private EternalEngineUser createOrFetchUser(GithubUser githubUser) {
        Optional<EternalEngineUser> optUser = userRepository.findByEmail(githubUser.getEmail());
        return optUser.orElseGet(() -> userRepository.save(EternalEngineUser.createFromGithubUser(githubUser)));
    }

    private GithubTokenResponse fetchToken(String tokenUrl) {

        RestTemplate template = new RestTemplate();
        GithubTokenResponse token = template.getForObject(tokenUrl, GithubTokenResponse.class);
        if (token == null) throw new WindflowWebException(WindflowError.ERROR_010, "No response to token request");
        if (token.getError() != null) {
            throw new WindflowWebException(WindflowError.ERROR_010, token.getError() + ": " + token.getErrorDescription());
        }
        return token;

    }

    /* private methods */

    private void checkForErrors(HttpServletRequest request) {
        if (request.getParameter("error") != null) {
            String errorString = request.getParameter("error") + ": " + request.getParameter("error_description");
            throw new WindflowWebException(WindflowError.ERROR_010, errorString);
        }
    }

    private GithubUser fetchUserData(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String userInfoUrl = "https://api.github.com/user";
        ResponseEntity<GithubUser> userInfoEntity = new RestTemplate().exchange(userInfoUrl, HttpMethod.GET, entity, GithubUser.class);
        return userInfoEntity.getBody();
    }

    private Cookie createCookie(UUID uuid) {
        Cookie cookie = new Cookie("token_exchange", Base64.getEncoder().encodeToString(uuid.toString().getBytes()));
        //cookie.setDomain(GITHUB_COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setMaxAge(30);
        return cookie;
    }

    /* Error handling */

    @ExceptionHandler(WindflowWebException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpError handleWindflowWebException(WindflowWebException windEx) {
        windEx.printStackTrace();
        return new HttpError(HttpStatus.NOT_FOUND.value(), windEx.getWindflowError(), windEx.getDetailOnly());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpError handleGeneralException(Exception ex) {
        ex.printStackTrace();
        return new HttpError(HttpStatus.INTERNAL_SERVER_ERROR.value(), WindflowError.ERROR_009, ex.getMessage());
    }

}

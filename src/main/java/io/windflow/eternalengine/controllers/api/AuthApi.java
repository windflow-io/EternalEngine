package io.windflow.eternalengine.controllers.api;

import io.windflow.eternalengine.beans.GithubTokenResponse;
import io.windflow.eternalengine.beans.GithubUser;
import io.windflow.eternalengine.beans.dto.Token;
import io.windflow.eternalengine.entities.EternalEngineUser;
import io.windflow.eternalengine.entities.Session;
import io.windflow.eternalengine.error.EternalEngineAuthException;
import io.windflow.eternalengine.error.EternalEngineError;

import io.windflow.eternalengine.persistence.SessionRepository;
import io.windflow.eternalengine.persistence.UserRepository;
import io.windflow.eternalengine.beans.dto.HttpError;
import io.windflow.eternalengine.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@PropertySource("classpath:openid.${spring.profiles.active}.properties")
public class AuthApi {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Value("${eternalengine.auth.github_client_id}")
    String GITHUB_CLIENT_ID;

    @Value("${eternalengine.auth.github_client_secret}")
    String GITHUB_CLIENT_SECRET;

    @Value("${eternalengine.auth.github_auth_domain}")
    String GITHUB_CALLBACK_DOMAIN;

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
    public void redirectUserToGitHub(HttpServletRequest request, HttpServletResponse response) throws EternalEngineAuthException {

        final String GITHUB_CALLBACK_URL = GITHUB_CALLBACK_DOMAIN + "/api/auth/github/callback";

        String referer = request.getHeader("referer");

        if (referer == null) throw new EternalEngineAuthException(EternalEngineError.ERROR_009, "Auth refused to redirect use to github without referer header", null);

        String state = URLEncoder.encode(referer, StandardCharsets.UTF_8);

        final String authRedirectUrl = GITHUB_LOGIN_URL + "?client_id=" + GITHUB_CLIENT_ID + "&scope=" + SCOPE + "&state=" + state + "&allow_signup=" + GITHUB_ALLOW_SIGNUP + "&redirect_uri=" + GITHUB_CALLBACK_URL;

        try {
            response.sendRedirect(authRedirectUrl);
        } catch (IOException ex) {
            throw new EternalEngineAuthException(EternalEngineError.ERROR_001, "Could not send redirect", ex, state);
        }
    }

    /**
     * Callback URL should be: [protocol://domain:port]/api/auth/github/callback
     * @param request
     * @throws EternalEngineAuthException
     */
    @GetMapping("/api/auth/github/callback")
    public void receiveUserFromGitHub(HttpServletRequest request, HttpServletResponse response) throws EternalEngineAuthException {

        final String GITHUB_CALLBACK_URL = GITHUB_CALLBACK_DOMAIN + "/api/auth/github/callback";
        final String code = request.getParameter("code");
        final String tokenUrl = GITHUB_TOKEN_URL + "?client_id=" + GITHUB_CLIENT_ID + "&client_secret=" + GITHUB_CLIENT_SECRET + "&code=" + code + "&redirect_uri=" + GITHUB_CALLBACK_URL;
        final String referer = request.getParameter("state");

        checkForErrors(request, referer);
        GithubTokenResponse token = fetchToken(tokenUrl);
        GithubUser githubUser = fetchUserData(token.getAccessToken());

        log.debug("We have a github user:");
        log.debug(githubUser.toString());

        EternalEngineUser user = createOrFetchUser(githubUser);

        log.debug("We've created a Eternal Engine User from the github user:");
        log.debug(user.toString());
        log.debug("Creating a session for this login:");

        Session session = sessionRepository.save(new Session(user.getId(), request.getRemoteAddr()));

        log.debug(session.toString());

        try {
            log.debug("Creating a cookie with the session UUID base 64 encoded session UUID in it. UUID is: " + session.getId());
            response.addCookie(createCookie(session.getId()));
            log.debug("Redirecting, cookie and all to: " + referer);
            response.sendRedirect(referer);
        } catch (IOException ex) {
            throw new EternalEngineAuthException(EternalEngineError.ERROR_001, "Could not send redirect", ex, referer);
        }

    }

    @GetMapping("/api/auth/github/exchange/{exchangeToken}")
    @Transactional
    protected Token tokenExchange(HttpServletRequest request, HttpServletResponse response, @PathVariable("exchangeToken") String exchangeToken) {
        String sessionId = new String (Base64.getDecoder().decode(exchangeToken));
        EternalEngineUser user = authService.exchangeToken(sessionId, request.getRemoteAddr());
        return authService.createJWT(user);
    }

    /* private methods */

    private EternalEngineUser createOrFetchUser(GithubUser githubUser) {
        Optional<EternalEngineUser> optUser = userRepository.findByEmail(githubUser.getEmail());
        return optUser.orElseGet(() -> userRepository.save(EternalEngineUser.createFromGithubUser(githubUser)));
    }

    private GithubTokenResponse fetchToken(String tokenUrl) {

        RestTemplate template = new RestTemplate();
        GithubTokenResponse token = template.getForObject(tokenUrl, GithubTokenResponse.class);
        if (token == null) throw new EternalEngineAuthException(EternalEngineError.ERROR_011, "No response to token request", null);
        if (token.getError() != null) {
            throw new EternalEngineAuthException(EternalEngineError.ERROR_011, token.getError() + ": " + token.getErrorDescription(), null);
        }
        return token;

    }

    private void checkForErrors(HttpServletRequest request, String referer) {
        if (request.getParameter("error") != null) {
            String errorString = request.getParameter("error") + ": " + request.getParameter("error_description");
            throw new EternalEngineAuthException(EternalEngineError.ERROR_011, errorString, referer);
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

    @ExceptionHandler(EternalEngineAuthException.class)
    public ResponseEntity<String> handleWindflowAuthException(EternalEngineAuthException windEx, HttpServletResponse response) {
        windEx.printStackTrace();
        try {
            String referer = windEx.getReferer();
            if (referer == null) throw new IOException(windEx);
            response.sendRedirect(referer + "?status=failed&error=" + URLEncoder.encode(windEx.getMessage(), StandardCharsets.UTF_8));
            return null;
        } catch (IOException ioe) {
            try {
                response.sendRedirect("/error?title=Internal+Server+Error&description=" + URLEncoder.encode(EternalEngineError.ERROR_014.name()  + ": " + EternalEngineError.ERROR_011.getTitle(), StandardCharsets.UTF_8) + "&detail=" + ioe.getCause().getMessage());
                return null;
            } catch (IOException ioe2) {
                String error = new HttpError(HttpStatus.INTERNAL_SERVER_ERROR.value(), EternalEngineError.ERROR_014, ioe.getMessage() + " and " + windEx.getMessage()).toString();
                return new ResponseEntity<String>(error, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex, HttpServletResponse response) {
        ex.printStackTrace();
        final String description = URLEncoder.encode(EternalEngineError.ERROR_011.name() + ": " + EternalEngineError.ERROR_011.getTitle(), StandardCharsets.UTF_8);
        final String detail = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
        try {
            response.sendRedirect("/error?title=Internal+Server+Error&description=" + description + "&detail=" + detail);
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            String error = new HttpError(HttpStatus.INTERNAL_SERVER_ERROR.value(), EternalEngineError.ERROR_014, ioe.getMessage() + " and " + ex.getMessage()).toString();
            return new ResponseEntity<String>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

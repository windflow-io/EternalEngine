package io.windflow.eternalengine.controllers.api;

import io.windflow.eternalengine.beans.GithubTokenResponse;
import io.windflow.eternalengine.beans.GithubUserResponse;
import io.windflow.eternalengine.error.WindflowError;
import io.windflow.eternalengine.error.WindflowWebException;

import io.windflow.eternalengine.utils.HttpError;
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

@RestController
@PropertySource("classpath:secret.properties")
public class AuthApi {

    @Value("${io.windflow.auth.github_client_id}")
    String GITHUB_CLIENT_ID;

    @Value("${io.windflow.auth.github_client_secret}")
    String GITHUB_CLIENT_SECRET;

    final String GITHUB_ALLOW_SIGNUP = "true";
    final String SCOPE = "read:user+user:email";

    private final String GITHUB_LOGIN_URL = "https://github.com/login/oauth/authorize";
    private final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private final String GITHUB_CALLBACK_PATH = "/api/auth/github/callback"; /*@TODO: UNHARDCODE THIS */

    @GetMapping("/api/auth/github")
    public void redirectUserToGitHub(HttpServletRequest request, HttpServletResponse response) throws WindflowWebException {

        String referer = request.getHeader("referer");

        if (referer == null) throw new WindflowWebException(WindflowError.ERROR_008, "Auth refused to redirect use to github without referer header");

        String githubCallbackUrl = getDomain() + referer.GITHUB_CALLBACK_PATH;
        String state = URLEncoder.encode(referer, StandardCharsets.UTF_8);
        final String authRedirectUrl = GITHUB_LOGIN_URL + "?client_id=" + GITHUB_CLIENT_ID + "&scope=" + SCOPE + "&state=" + state + "&allow_signup=" + GITHUB_ALLOW_SIGNUP + "&redirect_uri=" + githubCallbackUrl;

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

        System.out.println(fetchUserData(token.getAccessToken()));

        try {
            response.sendRedirect(request.getParameter("state"));
        } catch (IOException ex) {
            throw new WindflowWebException(WindflowError.ERROR_001, "Could not send redirect", ex);
        }

    }

    private GithubUserResponse fetchUserData(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + token);
        HttpEntity entity = new HttpEntity(headers);
        String userInfoUrl = "https://api.github.com/user";
        ResponseEntity<GithubUserResponse> userInfoEntity = new RestTemplate().exchange(userInfoUrl, HttpMethod.GET, entity, GithubUserResponse.class);
        return userInfoEntity.getBody();
    }

    private getDomain(String url ) {
        String[] parts = url.split("/");
        return parts[0] + parts[1]
    }

    @ExceptionHandler(WindflowWebException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpError handleWindflowWebException(WindflowWebException windEx) {
        windEx.printStackTrace();
        return new HttpError(HttpStatus.NOT_FOUND.value(), windEx.getWindflowError(), windEx.getDetailOnly());
    }

}

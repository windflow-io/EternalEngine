package io.windflow.eternalengine.extensions.api;

import io.windflow.eternalengine.extensions.framework.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public class WindflowOpenIdExtension<T extends Plugin> extends Plugin implements Requestable, Respondable, Actionable, Datafiable {

    HttpServletRequest request;
    HttpServletResponse response;
    HashMap<String, String> keyValues = new HashMap<>();

    final String REDIRECT_TO_GITHUB = "github_redirect";
    final String TEST_CLASS_LOADED = "test_class_loaded";

    @Override
    public void injectRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void injectResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public String performAction(String actionName, Object data) throws Exception {

        logger.info("Extension Performing Action");

        switch (actionName) {
            case REDIRECT_TO_GITHUB:
                final String base_url = keyValues.get("github_base_url");
                final String client_id = keyValues.get("github_client_id");

                /*@TODO: When instance cached, generate random "state", store it, check it on return.*/
                final String state = keyValues.get("01123581321345589");

                final String allow_signup = keyValues.get("github_allow_signup");
                final String scope = keyValues.get("github_scope");

                final String redirectUrl = base_url + "?client_id=" + client_id + "&scope=" + scope + "&state=" + state + "&allow_signup=" + allow_signup;
                response.sendRedirect(redirectUrl);

                break;
            case TEST_CLASS_LOADED: {
                return "{\"status\":\"Class loaded ok\"}";
            }
            default:
                return "{\"status\":\"Error. Action not recognised\"}";
        }

        return null;
    }

    @Override
    public void injectData(HashMap<String, String> keyValues) {
        this.keyValues = keyValues;
    }
}

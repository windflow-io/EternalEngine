package io.windflow.eternalengine.extensions.api;

import io.windflow.eternalengine.extensions.framework.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Test me: auth.windflow.local:8080/api/extensions/io.windflow.eternalengine.extensions.api.ProxyExtension/forward?url=https://www.google.com
 * @param <T>
 */
public class ProxyExtension<T extends Plugin> extends Plugin implements Requestable, Respondable, Actionable {

    HttpServletRequest request;
    HttpServletResponse response;

    @Override
    public String performAction(String actionName, Object data) throws ExtensionException {
        if (actionName.equals("forward")) {
            String url = request.getParameter("url");

            for (Enumeration<?> e = request.getHeaderNames(); e.hasMoreElements();) {
                String nextHeaderName = (String) e.nextElement();
                String headerValue = request.getHeader(nextHeaderName);
            }

            try {
                response.sendRedirect(url);
            } catch (IOException ex) {
                throw new ExtensionException("Could not redirect browser.", ex);
            }
        } else {
            throw new ExtensionException("The only action supported by this plugin is 'forward'");
        }
        return "Hi";
    }

    @Override
    public void injectRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void injectResponse(HttpServletResponse response) {
        this.response = response;
    }
}

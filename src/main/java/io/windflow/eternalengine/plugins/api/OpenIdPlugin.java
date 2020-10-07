package io.windflow.eternalengine.plugins.api;

import io.windflow.eternalengine.plugins.framework.Plugin;
import io.windflow.eternalengine.plugins.framework.RequestHandler;
import io.windflow.eternalengine.plugins.framework.ResponseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OpenIdPlugin<T extends Plugin> extends Plugin
        implements RequestHandler, ResponseHandler {

    HttpServletRequest request;
    HttpServletResponse response;

    public String sayHello() {
        return "Hello World!";
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

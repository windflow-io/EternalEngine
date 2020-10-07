package io.windflow.eternalengine.plugins.framework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ResponseHandler {

    void injectResponse(HttpServletResponse response);

}

package io.windflow.eternalengine.plugins.framework;

import javax.servlet.http.HttpServletRequest;

public interface RequestHandler {

    void injectRequest(HttpServletRequest request);

}

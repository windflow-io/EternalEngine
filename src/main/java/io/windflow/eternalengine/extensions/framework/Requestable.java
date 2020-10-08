package io.windflow.eternalengine.extensions.framework;

import javax.servlet.http.HttpServletRequest;

public interface Requestable {

    void injectRequest(HttpServletRequest request);

}

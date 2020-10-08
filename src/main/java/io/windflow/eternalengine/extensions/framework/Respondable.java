package io.windflow.eternalengine.extensions.framework;

import javax.servlet.http.HttpServletResponse;

public interface Respondable {

    void injectResponse(HttpServletResponse response);

}

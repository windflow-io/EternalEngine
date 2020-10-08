package io.windflow.eternalengine.extensions.framework;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public interface Datafiable {

    void injectData(HashMap<String, String> keyValues);
}

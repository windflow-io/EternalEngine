package io.windflow.eternalengine.extensions.framework;

public interface Actionable {

    String performAction (String actionName, Object data) throws Exception;
}

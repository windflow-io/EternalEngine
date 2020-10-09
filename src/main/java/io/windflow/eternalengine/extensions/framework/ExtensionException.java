package io.windflow.eternalengine.extensions.framework;

public class ExtensionException extends Exception {

    public ExtensionException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ExtensionException(String message) {
        super(message);
    }


}

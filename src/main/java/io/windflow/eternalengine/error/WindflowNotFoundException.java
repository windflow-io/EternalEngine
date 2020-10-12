package io.windflow.eternalengine.error;

/**
 * @TODO: Refactor to "EternalEngine" as opposed to "Windflow"
 */
public class WindflowNotFoundException extends WindflowBaseException {
    public WindflowNotFoundException(WindflowError windflowError) {
        super(windflowError);
    }

    public WindflowNotFoundException(WindflowError windflowError, String errorDetail) {
        super(windflowError, errorDetail);
    }
}

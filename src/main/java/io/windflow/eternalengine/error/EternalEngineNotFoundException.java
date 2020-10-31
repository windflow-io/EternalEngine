package io.windflow.eternalengine.error;

/**
 * @TODO: Refactor to "EternalEngine" as opposed to "Windflow"
 */
public class EternalEngineNotFoundException extends EternalEngineBaseException {
    public EternalEngineNotFoundException(EternalEngineError eternalEngineError) {
        super(eternalEngineError);
    }

    public EternalEngineNotFoundException(EternalEngineError eternalEngineError, String errorDetail) {

        super(eternalEngineError, errorDetail);
    }
}

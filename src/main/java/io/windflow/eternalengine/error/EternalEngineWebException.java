package io.windflow.eternalengine.error;

/**
 * @TODO: Refactor to "EternalEngine" as opposed to "Windflow"
 */
public class EternalEngineWebException extends EternalEngineBaseException {

    public EternalEngineWebException(EternalEngineError eternalEngineError) {
        super(eternalEngineError);
    }

    public EternalEngineWebException(EternalEngineError eternalEngineError, String errorDetail) {
        super(eternalEngineError, errorDetail);
    }

    public EternalEngineWebException(EternalEngineError eternalEngineError, String errorDetail, Exception ex) {
        super(eternalEngineError, errorDetail, ex);
    }

}

package io.windflow.eternalengine.error;

public class EternalEngineAuthException extends EternalEngineWebException {

    private String referer;

    public EternalEngineAuthException(EternalEngineError eternalEngineError, String errorDetail, String referer) {
        super(eternalEngineError, errorDetail);
        this.referer = referer;
    }

    public EternalEngineAuthException(EternalEngineError eternalEngineError, String errorDetail, Exception ex, String referer) {
        super(eternalEngineError, errorDetail, ex);
        this.referer = referer;
    }

    public String getReferer() {
        return referer;
    }

}

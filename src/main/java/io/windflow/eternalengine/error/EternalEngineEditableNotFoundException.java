package io.windflow.eternalengine.error;

public class EternalEngineEditableNotFoundException extends EternalEngineNotFoundException {

    String siteId;

    public EternalEngineEditableNotFoundException(EternalEngineError eternalEngineError, String siteId) {
        super(eternalEngineError);
        this.siteId = siteId;
    }

    public EternalEngineEditableNotFoundException(EternalEngineError eternalEngineError, String errorDetail, String sideId) {
        super(eternalEngineError, errorDetail);
        System.out.println("WORKING " + sideId);
        this.siteId = sideId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
}

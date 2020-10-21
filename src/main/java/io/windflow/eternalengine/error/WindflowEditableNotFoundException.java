package io.windflow.eternalengine.error;

public class WindflowEditableNotFoundException extends WindflowNotFoundException {

    String siteId;

    public WindflowEditableNotFoundException(WindflowError windflowError, String siteId) {
        super(windflowError);
        this.siteId = siteId;
    }

    public WindflowEditableNotFoundException(WindflowError windflowError, String errorDetail, String sideId) {
        super(windflowError, errorDetail);
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

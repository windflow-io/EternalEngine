package io.windflow.eternalengine.beans.dto;

import io.windflow.eternalengine.error.WindflowError;

public class HttpError {

    Integer httpStatus;
    String errorCode;
    String errorTitle;
    String errorDescription;
    String errorDetail;
    String siteId;

    public HttpError(Integer httpStatus, WindflowError windflowError, String errorDetail) {
        this.httpStatus = httpStatus;
        this.errorCode = windflowError.name();
        this.errorTitle = windflowError.getTitle();
        this.errorDescription = windflowError.getDescription();
        this.errorDetail = errorDetail;
    }

    public HttpError(Integer httpStatus, WindflowError windflowError, String errorDetail, String siteId) {
        this.httpStatus = httpStatus;
        this.errorCode = windflowError.name();
        this.errorTitle = windflowError.getTitle();
        this.errorDescription = windflowError.getDescription();
        this.errorDetail = errorDetail;
        this.siteId = siteId;
    }

    /*** Getters and Setters ***/

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setPageHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorTitle() {
        return errorTitle;
    }

    public void setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
}

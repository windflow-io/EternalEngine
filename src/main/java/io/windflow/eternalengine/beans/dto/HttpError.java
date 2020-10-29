package io.windflow.eternalengine.beans.dto;

import io.windflow.eternalengine.error.EternalEngineError;
import io.windflow.eternalengine.utils.JsonStringifiable;

public class HttpError extends JsonStringifiable {

    Integer httpStatus;
    String errorCode;
    String errorTitle;
    String errorDescription;
    String errorDetail;

    public HttpError(Integer httpStatus, EternalEngineError eternalEngineError, String errorDetail) {
        this.httpStatus = httpStatus;
        this.errorCode = eternalEngineError.name();
        this.errorTitle = eternalEngineError.getTitle();
        this.errorDescription = eternalEngineError.getDescription();
        this.errorDetail = errorDetail;
    }

    public HttpError(Integer httpStatus, EternalEngineError eternalEngineError, String errorDetail, String siteId) {
        this.httpStatus = httpStatus;
        this.errorCode = eternalEngineError.name();
        this.errorTitle = eternalEngineError.getTitle();
        this.errorDescription = eternalEngineError.getDescription();
        this.errorDetail = errorDetail;
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

}

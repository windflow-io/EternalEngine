package io.windflow.eternalengine.error;

/**
 * @TODO: Refactor to "EternalEngine" as opposed to "Windflow"
 */
public class EternalEngineBaseException extends RuntimeException {

    EternalEngineError eternalEngineError;
    String errorDetail = null;

    public EternalEngineBaseException(EternalEngineError eternalEngineError) {
        this.eternalEngineError = eternalEngineError;
    }

    public EternalEngineBaseException(EternalEngineError eternalEngineError, String errorDetail) {
        this.eternalEngineError = eternalEngineError;
        this.errorDetail = errorDetail;
    }

    public EternalEngineBaseException(EternalEngineError eternalEngineError, String errorDetail, Exception ex) {
        super(ex);
        this.eternalEngineError = eternalEngineError;
        this.errorDetail = errorDetail;
    }

    @Override
    public String getMessage() {
        return eternalEngineError.title + ": " + eternalEngineError.description + (errorDetail != null ?  ": " + errorDetail : "");
    }

    public String getDetailOnly() {
        return eternalEngineError + ": " + eternalEngineError.getTitle() + ": " + errorDetail;
    }


    /** Getters and Setters **/

    public EternalEngineError getWindflowError() {
        return eternalEngineError;
    }

    public void setWindflowError(EternalEngineError eternalEngineError) {
        this.eternalEngineError = eternalEngineError;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }
}

package io.windflow.eternalengine.error;

/**
 * @TODO: Refactor to "EternalEngine" as opposed to "Windflow"
 */
public class WindflowBaseException extends RuntimeException {

    WindflowError windflowError;
    String errorDetail = null;

    public WindflowBaseException(WindflowError windflowError) {
        this.windflowError = windflowError;
    }

    public WindflowBaseException(WindflowError windflowError, String errorDetail) {
        this.windflowError = windflowError;
        this.errorDetail = errorDetail;
    }

    public WindflowBaseException(WindflowError windflowError, String errorDetail, Exception ex) {
        super(ex);
        this.windflowError = windflowError;
        this.errorDetail = errorDetail;
    }

    @Override
    public String getMessage() {
        return windflowError.title + ": " + windflowError.description + (errorDetail != null ?  ": " + errorDetail : "");
    }

    public String getDetailOnly() {
        return windflowError + ": " + windflowError.getTitle() + ": " + errorDetail;
    }


    /** Getters and Setters **/

    public WindflowError getWindflowError() {
        return windflowError;
    }

    public void setWindflowError(WindflowError windflowError) {
        this.windflowError = windflowError;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }
}

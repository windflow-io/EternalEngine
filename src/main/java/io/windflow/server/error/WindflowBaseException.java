package io.windflow.server.error;

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

    @Override
    public String getMessage() {
        return windflowError.title + ": " + windflowError.description + (errorDetail != null ?  ": " + errorDetail : "");
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

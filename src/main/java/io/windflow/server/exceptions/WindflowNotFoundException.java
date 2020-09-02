package io.windflow.server.exceptions;

public class WindflowNotFoundException extends RuntimeException {

    WindflowError windflowError;
    String errorDetail = null;

    public WindflowNotFoundException(WindflowError windflowError) {
        this.windflowError = windflowError;
    }

    public WindflowNotFoundException(WindflowError windflowError, String errorDetail) {
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

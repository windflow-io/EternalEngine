package io.windflow.server.error;

public class WindflowWebException extends WindflowBaseException {

    public WindflowWebException(WindflowError windflowError) {
        super(windflowError);
    }

    public WindflowWebException(WindflowError windflowError, String errorDetail) {
        super(windflowError, errorDetail);
    }
}

package io.windflow.server.error;

public class WindflowNotFoundException extends WindflowBaseException {
    public WindflowNotFoundException(WindflowError windflowError) {
        super(windflowError);
    }

    public WindflowNotFoundException(WindflowError windflowError, String errorDetail) {
        super(windflowError, errorDetail);
    }
}

package com.notquitehere.selfdriver.util;


        import java.io.Serializable;

public class ErrorWithSeverity implements Serializable {

    public final String errorMessage;
    public final Severity severity;
    public transient final Throwable throwable;

    public ErrorWithSeverity(
            String errorMessage,
            Severity severity,
            Throwable throwable
    ) {
        this.errorMessage = errorMessage;
        this.severity = severity;
        this.throwable = throwable;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}

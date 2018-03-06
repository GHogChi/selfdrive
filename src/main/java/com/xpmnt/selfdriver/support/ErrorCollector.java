package com.xpmnt.selfdriver.support;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.xpmnt.selfdriver.support.Severity.OKAY;
import static java.text.MessageFormat.format;

/**
 * The ErrorCollector can be seen as an extension of Result&lt;Void&gt;
 * for cases where multiple errors may be found.
 *
 * NOTE: Throwables may be set and retrieved but they are not serializable.
 * This is a security measure to prevent external visibility of stacktraces.
 * Stacktraces should only be visible in testing and debugging.
 * <p>Created by trose004 on 3/7/17.</p>
 *
 * @see Result
 */
public class ErrorCollector implements Serializable {
    private final List<ErrorWithSeverity> errors = new LinkedList<>();
    private Severity maxSeverity = OKAY;

    public List<ErrorWithSeverity> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public ErrorCollector add(String error, Severity severity) {
        add(new ErrorWithSeverity(error, severity, null));
        return this;
    }

    protected void add(ErrorWithSeverity errorWithSeverity) {
        errors.add(errorWithSeverity);
        if (errorWithSeverity.severity.ordinal() > maxSeverity.ordinal()) {
            maxSeverity = errorWithSeverity.severity;
        }
    }

    public ErrorCollector add(Throwable throwable, Severity severity) {
        return add(
                formatThrowable(throwable), severity
        );
    }

    public ErrorCollector add(
            String message,
            Throwable throwable, Severity severity
    ) {
        add(new ErrorWithSeverity(
                message + " due to: " + formatThrowable(throwable),
                severity,
                throwable
        ));
        return this;
    }

    public static String formatThrowable(Throwable throwable) {
        return throwable
                .getClass()
                .getName() + ": " +
                throwable.getLocalizedMessage();
    }

    public ErrorCollector addFormatted(
            Severity severity, String formatString, Object... args
    ) {
        return add(format(formatString, args), severity);
    }

    public int count() {
        return errors.size();
    }

    public Severity getMaxSeverity() {
        return maxSeverity;
    }

    public boolean found() {
        return !errors.isEmpty();
    }

    public String asString() {
        if (found()) {
            StringBuilder sb = new StringBuilder();
            for (ErrorWithSeverity error : errors) {
                sb
                        .append(error.errorMessage)
                        .append(" [severity: ")
                        .append(error.severity.name())
                        .append("]\n");
            }
            return sb.toString();
        }
        return "";
    }
}

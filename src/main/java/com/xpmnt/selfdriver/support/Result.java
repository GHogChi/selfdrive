package com.xpmnt.selfdriver.support;


import java.io.Serializable;

import static java.text.MessageFormat.format;

/**
 * Modern replacement for Java exceptions.
 * Generic type T is the type of the object to be returned: the output of the
 * function(/method). If there is no object, use "Void".
 * <p>
 * <p>Created 11/17/16.</p>
 *
 * @see <a href="http://www.lighterra.com/papers/exceptionsharmful/">Exception
 * Handling Considered Harmful
 * </a>
 */
public class Result<T> implements Serializable {
    private final boolean ok;
    private final T output;
    private final String errorMsg;
    private final Throwable throwable;
    private final ErrorCollector errors;

    /**
     * For serialization only.
     */
    public Result() {
        ok = false;
        output = null;
        errorMsg = null;
        throwable = null;
        errors = null;
    }

    private Result(
        String errorMsg,
        Throwable throwable,
        ErrorCollector errors
    ) {
        this.errorMsg = errorMsg;
        this.throwable = throwable;
        this.errors = errors;
        ok = false;
        output = null;
    }

    private Result(T output) {
        this.output = output;
        this.throwable = null;
        ok = true;
        errorMsg = null;
        errors = null;
    }

    public boolean succeeded() {
        return ok;
    }

    public boolean failed() {
        return !ok;
    }

    public ErrorCollector getCollectedErrors() {
        return errors;
    }

    public static <T> Result<T> success(T output) {
        return new Result<>(output);
    }

    public static Result<Void> success() {
        return new Result<>(null);
    }

    public static <T> Result<T> failure(ErrorCollector errors) {
        return new Result<>(errors.asString(), null, errors);
    }

    public static <T> Result<T> failure(String errorMsg) {
        return new Result<>(errorMsg, null, null);
    }

    public static <T> Result<T> failure(String errorMsg, Throwable throwable) {
        return new Result<>(errorMsg, throwable, null);
    }

    public static <T, U> Result<T> failure(Result<U> result) {
        if (!result.failed()) {
            throw new RuntimeException("Expected failed result, got success.");
        }
        return failure(result.getErrorMessage(), result.throwable);
    }

    public static Result failure(Throwable e, String format, String... args) {
        return failure(format(format, (Object[]) args), e);
    }

    public static Result failureFormatted(String format, Object... args) {
        return failure(format(format, args));
    }

    public T getOutput() {
        if (succeeded()) {
            return output;
        }
        throw new IllegalStateException("Operation failed: no output to " +
            "return.");
    }

    public String getErrorMessage() {
        if (failed()) {
            return errorMsg;
        }
        throw new IllegalStateException("Operation succeeded: no error " +
            "message.");
    }

    public Throwable getThrowable() {
        if (failed()) {
            return throwable;
        }
        throw new IllegalStateException("Operation succeeded: no throwable.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Result)) {
            return false;
        }

        Result<?> result = (Result<?>) o;

        return ok == result.ok &&
            (output != null ? output.equals(result.output) :
                result.output == null) &&
            (errorMsg != null ? errorMsg.equals(result.errorMsg) :
                result.errorMsg == null) &&
            (throwable != null ? throwable.equals(result.throwable) :
                result.throwable == null);
    }

    @Override
    public int hashCode() {
        int result = (ok ? 1 : 0);
        result = 31 * result + (output != null ? output.hashCode() : 0);
        result = 31 * result + (errorMsg != null ? errorMsg.hashCode() : 0);
        result = 31 * result + (throwable != null ? throwable.hashCode() : 0);
        return result;
    }
}

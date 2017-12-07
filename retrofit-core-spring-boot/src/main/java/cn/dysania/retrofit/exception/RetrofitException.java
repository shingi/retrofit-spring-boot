package cn.dysania.retrofit.exception;

import static java.lang.String.format;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * retrofit exception
 *
 * @author baitouweng
 */
public class RetrofitException extends RuntimeException {

    private static final long serialVersionUID = 0;
    private int status;

    protected RetrofitException(String message, Throwable cause) {
        super(message, cause);
    }

    protected RetrofitException(String message) {
        super(message);
    }

    protected RetrofitException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int status() {
        return this.status;
    }

    static RetrofitException errorReading(Request request, Response ignored, IOException cause) {
        return new RetrofitException(
                format("%s reading %s %s", cause.getMessage(), request.method(), request.url()),
                cause);
    }

    static RetrofitException errorExecuting(Request request, IOException cause) {
        return new RetryableException(
                format("%s executing %s %s", cause.getMessage(), request.method(), request.url()),
                cause,
                null);
    }
}

package cn.dysania.retrofit.exception;

import java.util.Date;

/**
 * 重试异常
 *
 * @author baitouweng
 */
public class RetryableException extends RetrofitException {

    private static final long serialVersionUID = 1L;

    private final Long retryAfter;

    public RetryableException(String message, Throwable cause, Date retryAfter) {
        super(message, cause);
        this.retryAfter = retryAfter != null ? retryAfter.getTime() : null;
    }

    public RetryableException(String message, Date retryAfter) {
        super(message);
        this.retryAfter = retryAfter != null ? retryAfter.getTime() : null;
    }

    public Date retryAfter() {
        return retryAfter != null ? new Date(retryAfter) : null;
    }
}

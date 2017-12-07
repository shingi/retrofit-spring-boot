package cn.dysania.retrofit.instrument.interceptor;

import static java.lang.String.format;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.dysania.retrofit.exception.RetryableException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 重试拦截器
 *
 * @author baitouweng
 */
public class RetryInterceptor implements Interceptor {

    private final Logger logger = LoggerFactory.getLogger(RetryInterceptor.class);

    private final Retryer defaultRetryer;

    public RetryInterceptor() {
        this(new Retryer.Default());
    }

    public RetryInterceptor(Retryer defaultRetryer) {
        this.defaultRetryer = defaultRetryer;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String idempotent = request.header("X-Idempotent");
        if (idempotent != null && "false".equals(idempotent)) {
            return chain.proceed(request);
        }
        return retryProceed(chain);
    }

    private Response retryProceed(Chain chain) throws IOException {
        Request request = chain.request();
        Retryer retryer = this.defaultRetryer.copy();
        while (true) {
            try {
                return chain.proceed(request);
            } catch (IOException e) {
                RetryableException retryableException = new RetryableException(
                        format("%s executing %s %s", e.getMessage(), request.method(),
                                request.url()),
                        e, null);
                retryer.continueOrPropagate(retryableException);
                if (logger.isWarnEnabled()) {
                    logger.warn("{} ---> RETRYING", retryableException.getMessage());
                }
            }
        }
    }
}

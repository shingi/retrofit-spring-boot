package cn.dysania.retrofit.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TODO 类描述
 *
 * @author baitouweng
 */
@ConfigurationProperties("retrofit.client")
public class OkHttpClientProperties {

    private static final int DEFAULT_CONNECTION_TIMEOUT = 2_000;
    private static final int DEFAULT_READ_TIMEOUT = 2_000;
    private static final int DEFAULT_WRITE_TIMEOUT = 2_000;

    private String traceUrl;
    private int connectTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private int readTimeout = DEFAULT_READ_TIMEOUT;
    private int writeTimeout = DEFAULT_WRITE_TIMEOUT;

    public String getTraceUrl() {
        return traceUrl;
    }

    public void setTraceUrl(String traceUrl) {
        this.traceUrl = traceUrl;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
}

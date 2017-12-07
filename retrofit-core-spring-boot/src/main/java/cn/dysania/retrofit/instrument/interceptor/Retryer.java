package cn.dysania.retrofit.instrument.interceptor;

import static java.util.concurrent.TimeUnit.SECONDS;

import cn.dysania.retrofit.exception.RetryableException;

/**
 * 重试机制
 *
 * @author baitouweng
 */
public interface Retryer {

    Retryer NEVER_RETRY = new Retryer() {
        @Override
        public void continueOrPropagate(RetryableException e) {
            throw e;
        }

        @Override
        public Retryer copy() {
            return this;
        }
    };

    void continueOrPropagate(RetryableException e);

    Retryer copy();

    /**
     * 重试机制的默认实现。
     */
    class Default implements Retryer {

        private final int maxAttempts;
        private final long period;
        private final long maxPeriod;
        int attempt;
        long sleptForMillis;

        public Default() {
            this(100, SECONDS.toMillis(1), 3);
        }

        public Default(long period, long maxPeriod, int maxAttempts) {
            this.period = period;
            this.maxPeriod = maxPeriod;
            this.maxAttempts = maxAttempts;
            this.attempt = 1;
        }

        protected long currentTimeMillis() {
            return System.currentTimeMillis();
        }

        public void continueOrPropagate(RetryableException e) {
            if (attempt++ >= maxAttempts) {
                throw e;
            }

            long interval;
            if (e.retryAfter() != null) {
                interval = e.retryAfter().getTime() - currentTimeMillis();
                if (interval > maxPeriod) {
                    interval = maxPeriod;
                }
                if (interval < 0) {
                    return;
                }
            } else {
                interval = nextMaxInterval();
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            sleptForMillis += interval;
        }

        @Override
        public Retryer copy() {
            return new Default(period, maxPeriod, maxAttempts);
        }

        long nextMaxInterval() {
            long interval = (long) (period * Math.pow(1.5, attempt - 1));
            return interval > maxPeriod ? maxPeriod : interval;
        }
    }
}

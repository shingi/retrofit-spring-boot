package cn.dysania.retrofit.hystrix.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.springframework.util.ReflectionUtils;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.exception.HystrixBadRequestException;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;

/**
 * hystrix call adapter
 *
 * @author baitouweng
 */
public class HystrixCallAdapter<R> implements CallAdapter<R, Object> {

    private final boolean isHystrixCommand;

    private final boolean isObservable;

    private final boolean isSingle;

    private final boolean isCompletable;

    private final boolean isBody;

    private final boolean isResponse;

    private final Type responseType;

    public HystrixCallAdapter(Type responseType, boolean isResponse, boolean isBody,
            boolean isHystrixCommand, boolean isObservable, boolean isSingle,
            boolean isCompletable) {
        this.responseType = responseType;
        this.isResponse = isResponse;
        this.isBody = isBody;
        this.isHystrixCommand = isHystrixCommand;
        this.isObservable = isObservable;
        this.isSingle = isSingle;
        this.isCompletable = isCompletable;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public Object adapt(Call<R> call) {
        // TODO Setter
        Request request = call.request();
        HystrixCommand hystrixCommand = new HystrixCommand(HystrixCommand.Setter.withGroupKey(
                HystrixCommandGroupKey.Factory.asKey("ExampleGroup")).andCommandKey(
                HystrixCommandKey.Factory.asKey("Github.fetchRepo")).andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(200000))) {
            @Override
            protected Object run() throws Exception {
                try {
                    Response<R> response = call.execute();
                    if (HystrixCallAdapter.this.isBody) {
                        return response.body();
                    }
                    return response;
                } catch (Exception e) {
                    // TODO 包装一个业务异常 不触发服务降级处理逻辑
                    throw new HystrixBadRequestException("bad request", e);
                }
            }

            @Override
            protected Object getFallback() {
                if (circuitBreaker.isOpen()){
                    // TODO 报警逻辑，抽象报警器 报警速率控制
                }
                return super.getFallback();
            }
        };

        if (isHystrixCommand) {
            return hystrixCommand;
        }

        if (isObservable) {
            // cold Observable
            return hystrixCommand.toObservable();
        }

        if (isSingle) {
            return hystrixCommand.toObservable().toSingle();
        }

        if (isCompletable) {
            return hystrixCommand.toObservable().toCompletable();
        }

        return hystrixCommand.execute();
    }
}

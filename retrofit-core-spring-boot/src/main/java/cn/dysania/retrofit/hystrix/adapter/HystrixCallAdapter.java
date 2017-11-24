package cn.dysania.retrofit.hystrix.adapter;

import java.lang.reflect.Type;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

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
        HystrixCommand hystrixCommand = new HystrixCommand(HystrixCommand.Setter.withGroupKey(
                HystrixCommandGroupKey.Factory.asKey("ExampleGroup")).andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter().withExecutionTimeoutEnabled(false))) {
            @Override
            protected Object run() throws Exception {
                Response<R> response = call.execute();
                if (HystrixCallAdapter.this.isBody) {
                    return response.body();
                }
                return response;
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

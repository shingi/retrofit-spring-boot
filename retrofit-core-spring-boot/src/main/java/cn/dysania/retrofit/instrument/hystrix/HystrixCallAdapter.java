package cn.dysania.retrofit.instrument.hystrix;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.exception.HystrixBadRequestException;

import cn.dysania.retrofit.exception.RetrofitBadRequestException;
import cn.dysania.retrofit.exception.RetrofitNotClasspathException;
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

    private final String commandGroup;

    public HystrixCallAdapter(Type responseType, boolean isResponse, boolean isBody,
            boolean isHystrixCommand, boolean isObservable, boolean isSingle,
            boolean isCompletable, String commandGroup) {
        this.responseType = responseType;
        this.isResponse = isResponse;
        this.isBody = isBody;
        this.isHystrixCommand = isHystrixCommand;
        this.isObservable = isObservable;
        this.isSingle = isSingle;
        this.isCompletable = isCompletable;
        this.commandGroup = commandGroup;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public Object adapt(Call<R> call) {

        HystrixCommand.Setter setter = obtainHystrixCommandSetter(call);

        HystrixCommand hystrixCommand = new HystrixCommand(setter) {
            @Override
            protected Object run() throws Exception {
                try {
                    Response<R> response = call.execute();
                    if (HystrixCallAdapter.this.isBody) {
                        return response.body();
                    }
                    return response;
                } catch (RetrofitBadRequestException e) {
                    //Do not trigger circuitBreaker
                    throw new HystrixBadRequestException(e.getMessage(), e.getCause());
                }
            }

            @Override
            protected Object getFallback() {
                if (circuitBreaker.isOpen()) {
                    // TODO report?
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

    private HystrixCommand.Setter obtainHystrixCommandSetter(Call<R> call) {
        String method = call.request().method();

        Assert.hasText(commandGroup, "commandGroup must be set");
        String relativeUrl;
        try {
            relativeUrl = obtainRelativeUrl(call);
        } catch (ClassNotFoundException e) {
            throw new RetrofitNotClasspathException("Please check retrofit in classpath", e);
        }

        String commandKey = method + "#" + relativeUrl;

        return HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(this.commandGroup))
                .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey));
    }

    private String obtainRelativeUrl(Call call) throws ClassNotFoundException {
        // OkHttpCall and ServiceMethod is Package accesscontrol
        Class<?> okHttpCallClass = Class.forName("retrofit2.OkHttpCall");
        Class<?> serviceMethodClass = Class.forName("retrofit2.ServiceMethod");

        Field serviceMethodField = ReflectionUtils.findField(okHttpCallClass, "serviceMethod");
        serviceMethodField.setAccessible(true);
        Object serviceMethod = ReflectionUtils.getField(serviceMethodField, call);

        Field relativeUrlField = ReflectionUtils.findField(serviceMethodClass, "relativeUrl");
        relativeUrlField.setAccessible(true);
        Object relativeUrl = ReflectionUtils.getField(relativeUrlField, serviceMethod);
        return (String) relativeUrl;
    }
}

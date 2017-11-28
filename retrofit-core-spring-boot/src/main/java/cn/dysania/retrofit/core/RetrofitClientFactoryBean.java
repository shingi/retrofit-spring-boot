package cn.dysania.retrofit.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import lombok.Setter;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * retrofit getHttpClient factory bean
 *
 * @author baitouweng
 */
@Setter
public class RetrofitClientFactoryBean implements FactoryBean<Object>, InitializingBean,
        ApplicationContextAware {

    private Class<?> type;

    private String name;

    private String url;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.name, "name must be set");
    }

    @Override
    public Object getObject() throws Exception {
        RetrofitContext context = applicationContext.getBean(RetrofitContext.class);

        Retrofit.Builder builder = retrofit(context);

        return builder.baseUrl(url).build().create(type);
    }

    protected <T> T getInstance(RetrofitContext context, Class<T> type) {
        T instance = context.getInstance(this.name, type);
        if (instance == null) {
            throw new IllegalStateException("No bean found of type " + type + " for "
                    + this.name);
        }
        return instance;
    }

    protected OkHttpClient getHttpClient(RetrofitContext context) {
        OkHttpClient.Builder builder = getInstance(context, OkHttpClient.Builder.class);

        return builder.build();
    }

    protected Retrofit.Builder retrofit(RetrofitContext context) {
        Retrofit.Builder builder = getInstance(context, Retrofit.Builder.class);

        OkHttpClient httpClient = getHttpClient(context);
        builder.client(httpClient);

        Converter.Factory converterFactory = getOptional(context, Converter.Factory.class);
        if (converterFactory != null) {
            builder.addConverterFactory(converterFactory);
        }

        CallAdapter.Factory callAdapterFactory = getOptional(context, CallAdapter.Factory.class);
        if (callAdapterFactory != null) {
            builder.addCallAdapterFactory(callAdapterFactory);
        }

        Call.Factory callFactory = getOptional(context, Call.Factory.class);
        if (callFactory != null) {
            builder.callFactory(callFactory);
        }
        return builder;
    }

    protected <T> T getOptional(RetrofitContext context, Class<T> type) {
        return context.getInstance(this.name, type);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

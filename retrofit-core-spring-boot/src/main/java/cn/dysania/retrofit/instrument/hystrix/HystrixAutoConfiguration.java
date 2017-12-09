package cn.dysania.retrofit.instrument.hystrix;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.netflix.hystrix.HystrixCommand;

import retrofit2.CallAdapter;

/**
 * @author baitouweng
 */
@Configuration
@ConditionalOnClass({ HystrixCommand.class })
public class HystrixAutoConfiguration {

    @Bean
    @Scope("prototype")
    @ConditionalOnProperty(name = "retrofit.hystrix.enabled", havingValue = "true")
    @ConditionalOnMissingBean
    public CallAdapter.Factory hystrixCallAdapterFactory() {
        return new HystrixCallAdapterFactory();
    }
}

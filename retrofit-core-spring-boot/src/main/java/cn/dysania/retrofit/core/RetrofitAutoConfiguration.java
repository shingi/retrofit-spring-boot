package cn.dysania.retrofit.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO 类描述
 *
 * @author baitouweng
 */
@Configuration
public class RetrofitAutoConfiguration {

    @Autowired(required = false)
    private List<RetrofitClientSpecification> configurations = new ArrayList<>();

    @Bean
    public RetrofitContext retrofitContext() {
        RetrofitContext context = new RetrofitContext();
        context.setConfigurations(this.configurations);
        return context;
    }
}

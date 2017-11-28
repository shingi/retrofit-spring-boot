package cn.dysania.retrofit.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

import cn.dysania.retrofit.core.EnableRetrofitClients;

/**
 * TODO 类描述
 *
 * @author baitouweng
 */
@SpringBootApplication
@EnableRetrofitClients
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean hystrixMetricsStreamServlet(){
        return new ServletRegistrationBean(new HystrixMetricsStreamServlet(), "/hystrix.stream");
    }
}

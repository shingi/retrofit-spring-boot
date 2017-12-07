package cn.dysania.retrofit.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * TODO 类描述
 *
 * @author liangtian
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EnableRetrofitClientsTest.Config.class)
@DirtiesContext
public class EnableRetrofitClientsTest {

    @Autowired
    private RetrofitContext retrofitContext;

    @Test
    public void converterFactory() {
        GsonConverterFactory.class.cast(
                retrofitContext.getInstance("foo", Converter.Factory.class));
    }

    @Configuration
    @Import({ RetrofitAutoConfiguration.class,RetrofitClientsConfiguration.class })
    static class Config {

    }
}

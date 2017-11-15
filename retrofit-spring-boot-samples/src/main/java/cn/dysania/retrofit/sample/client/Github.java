package cn.dysania.retrofit.sample.client;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.dysania.retrofit.core.RetrofitClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * TODO 类描述
 *
 * @author baitouweng
 */
@RetrofitClient(value = "github", url = "${github.api.url}", configuration = Github.GithubConfiguration.class)
public interface Github {

    @GET("users/{user}/repos")
    Call<List<Repo>> listRepos(@Path("user") String user);

    @Data
    class Repo {

        String name;

        String full_name;
    }

    @Configuration
    @Slf4j
    class GithubConfiguration {

        @Bean
        public Converter.Factory converterFactory() {
            log.info("start create GsonConverterFactory");
            return GsonConverterFactory.create();
        }
    }
}

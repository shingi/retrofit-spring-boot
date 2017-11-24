package cn.dysania.retrofit;

import java.util.List;

import com.netflix.hystrix.HystrixCommand;

import lombok.Data;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * TODO 类描述
 *
 * @author liangtian
 */
public interface Github {

    @GET("users/{user}/repos")
    HystrixCommand<List<Repo>> returnHystrixCommandBody(@Path("user") String user);

    @Data
    class Repo {

        String name;

        String full_name;
    }
}

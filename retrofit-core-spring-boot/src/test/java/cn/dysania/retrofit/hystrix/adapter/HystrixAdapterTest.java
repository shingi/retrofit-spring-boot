package cn.dysania.retrofit.hystrix.adapter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.netflix.hystrix.HystrixCommand;

import cn.dysania.retrofit.Github;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * TODO 类描述
 *
 * @author liangtian
 */
public class HystrixAdapterTest {

    private Github github;

    @Before
    public void init() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(new HysyrixCallAdapterFactory())
                .baseUrl("https://api.github.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.github = retrofit.create(Github.class);
    }

    @Test
    public void testReturnHystrixCommandBody() {
        HystrixCommand<List<Github.Repo>> hystrixCommand = github.returnHystrixCommandBody(
                "liangGTY");
        assertNotNull(hystrixCommand);
        List<Github.Repo> repos = hystrixCommand.execute();
        assertFalse(repos.isEmpty());
        repos.forEach(System.out::println);
    }
}

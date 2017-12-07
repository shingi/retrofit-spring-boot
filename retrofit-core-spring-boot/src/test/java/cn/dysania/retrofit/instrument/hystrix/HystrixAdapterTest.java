package cn.dysania.retrofit.instrument.hystrix;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.netflix.hystrix.HystrixCommand;

import lombok.Data;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 *
 * @author liangtian
 */
public class HystrixAdapterTest {

    @Rule
    public WireMockRule githubServer = new WireMockRule();

    private Github github;

    @Before
    public void init() {
        githubServer.stubFor(get(urlEqualTo(
                "/users/liangGTY/repos"))
                .willReturn(aResponse()
                        .withBody(
                                "{\"name\":\"retrofit-spring-boot\","
                                        + "\"full_name\":\"liangGTY/retrofit-spring-boot\"}")
                        .withStatus(200)
                ));

        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(new HysyrixCallAdapterFactory())
                .baseUrl("http://localhost:" + githubServer.port())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.github = retrofit.create(Github.class);
    }

    @Test
    public void testReturnHystrixCommandBody() {
        HystrixCommand<Github.Repo> hystrixCommand = github.returnHystrixCommandBody(
                "liangGTY");
        Github.Repo repos = hystrixCommand.execute();
        assertThat(repos).isNotNull();
    }

    @Test
    public void testReturnObservableBody() {
        Observable<Github.Repo> observable = github.returnObservable(
                "liangGTY");
        Github.Repo repos = observable.toBlocking().first();
        assertThat(repos).isNotNull();
    }

    interface Github {

        @GET("users/{user}/repos")
        HystrixCommand<Github.Repo> returnHystrixCommandBody(@Path("user") String user);

        @GET("users/{user}/repos")
        Observable<Github.Repo> returnObservable(@Path("user") String user);

        @Data
        class Repo {

            String name;

            String full_name;
        }
    }
}

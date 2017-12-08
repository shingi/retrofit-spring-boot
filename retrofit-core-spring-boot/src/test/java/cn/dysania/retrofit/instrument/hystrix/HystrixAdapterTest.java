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
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * @author liangtian
 */
public class HystrixAdapterTest {

    @Rule
    public WireMockRule githubServer = new WireMockRule();

    private Github github;

    @Before
    public void setUp() {
        githubServer.stubFor(get(urlEqualTo(
                "/users/liangGTY/repos"))
                .willReturn(aResponse()
                        .withBody(
                                "{\"name\":\"retrofit-spring-boot\","
                                        + "\"full_name\":\"liangGTY/retrofit-spring-boot\"}")
                        .withStatus(200)
                ));

        OkHttpClient client = new OkHttpClient.Builder().build();
        HystrixCallAdapterFactory callAdapterFactory = new HystrixCallAdapterFactory();

        callAdapterFactory.setCommandGroup("github");

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(callAdapterFactory)
                .baseUrl("http://localhost:" + githubServer.port())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.github = retrofit.create(Github.class);
    }

    @Test
    public void testReturnHystrixCommandBody() {
        HystrixCommand<Github.Repo> hystrixCommand = github.returnHystrixCommandBody("liangGTY");
        Github.Repo repo = hystrixCommand.execute();
        assertThat(repo).isNotNull();
        assertThat(repo.getName()).isEqualTo("retrofit-spring-boot");
    }

    @Test
    public void testReturnObservableBody() {
        Observable<Github.Repo> observable = github.returnObservable("liangGTY");
        Github.Repo repo = observable.toBlocking().first();
        assertThat(repo).isNotNull();
        assertThat(repo.getName()).isEqualTo("retrofit-spring-boot");
    }

    @Test
    public void testReturnSingle() {
        Single<Github.Repo> single = github.returnSingle("liangGTY");
        Github.Repo repo = single.toBlocking().value();
        assertThat(repo).isNotNull();
        assertThat(repo.getName()).isEqualTo("retrofit-spring-boot");
    }

    @Test
    public void testReturnCompletable() {
        Completable completable = github.returnCompletable("liangGTY");
        completable.await();
    }

    @Test
    public void testReturnBody() {
        Github.Repo repo = github.returnBody("liangGTY");
        assertThat(repo).isNotNull();
        assertThat(repo.getName()).isEqualTo("retrofit-spring-boot");
    }

    interface Github {

        @GET("users/{user}/repos")
        HystrixCommand<Repo> returnHystrixCommandBody(@Path("user") String user);

        @GET("users/{user}/repos")
        Observable<Repo> returnObservable(@Path("user") String user);

        @GET("users/{user}/repos")
        Single<Repo> returnSingle(@Path("user") String user);

        @GET("users/{user}/repos")
        Completable returnCompletable(@Path("user") String user);

        @GET("users/{user}/repos")
        Repo returnBody(@Path("user") String user);

        @Data
        class Repo {

            String name;

            String full_name;
        }
    }
}

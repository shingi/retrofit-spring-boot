package cn.dysania.retrofit.instrument.hystrix;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.netflix.hystrix.HystrixCommand;

import cn.dysania.retrofit.core.EnableRetrofitClients;
import cn.dysania.retrofit.core.RetrofitAutoConfiguration;
import cn.dysania.retrofit.core.RetrofitClient;
import cn.dysania.retrofit.core.RetrofitClientsConfiguration;
import lombok.Data;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @author liangtian
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HystrixWithAnnotionTest.Config.class, webEnvironment = SpringBootTest
        .WebEnvironment.NONE, properties = { "retrofit.hystrix.enabled=true" })
@DirtiesContext
public class HystrixWithAnnotionTest {

    @Rule
    public WireMockRule githubServer = new WireMockRule();

    @Autowired
    private Github github;

    @Before
    public void init() {
        githubServer.stubFor(get(urlEqualTo(
                "/users/liangGTY/repos"))
                .willReturn(aResponse()
                        .withBody("{\"name\":\"retrofit-spring-boot\","
                                + "\"full_name\":\"liangGTY/retrofit-spring-boot\"}")
                        .withStatus(200)
                ));
    }

    @Test
    public void testReturnHystrixCommandBody() {
        HystrixCommand<Github.Repo> hystrixCommand = github
                .returnHystrixCommandBody(
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

    @RetrofitClient(name = "github", url = "http://localhost:8080")
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

    @Configuration
    @EnableRetrofitClients
    @EnableAutoConfiguration
    @Import({ RetrofitAutoConfiguration.class, RetrofitClientsConfiguration.class })
    static class Config {

    }
}

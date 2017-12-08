package cn.dysania.retrofit.instrument.hystrix;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import cn.dysania.retrofit.core.RetrofitContext;
import lombok.Data;
import retrofit2.CallAdapter;
import retrofit2.http.GET;
import retrofit2.http.Path;

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

    @Autowired
    private RetrofitContext context;

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
    public void hystrixCallAdapterFactory() {
        HystrixCallAdapterFactory.class.cast(context.getInstance("foo", CallAdapter.Factory.class));
    }

    @Test
    public void testReturnHystrixCommandBody() {
        HystrixCommand<Github.Repo> hystrixCommand = github.returnHystrixCommandBody("liangGTY");
        assertThat(hystrixCommand.getCommandGroup().name()).isEqualTo("github");
        assertThat(hystrixCommand.getCommandKey().name()).isEqualTo("GET#users/{user}/repos");
        Github.Repo repos = hystrixCommand.execute();
        assertThat(repos).isNotNull();
    }

    @RetrofitClient(name = "github", url = "http://localhost:8080")
    interface Github {

        @GET("users/{user}/repos")
        HystrixCommand<Github.Repo> returnHystrixCommandBody(@Path("user") String user);

        @Data
        class Repo {

            String name;

            String full_name;
        }
    }

    @Configuration
    @EnableRetrofitClients
    @Import({ RetrofitAutoConfiguration.class, RetrofitClientsConfiguration.class })
    static class Config {

    }
}

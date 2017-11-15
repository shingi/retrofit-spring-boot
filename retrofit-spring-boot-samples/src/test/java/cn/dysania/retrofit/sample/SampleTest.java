package cn.dysania.retrofit.sample;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.dysania.retrofit.sample.client.Github;

import retrofit2.Call;

/**
 * TODO 类描述
 *
 * @author baitouweng
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RetrofitClientConfig.class)
public class SampleTest {

    @Autowired
    private Github github;

    @Test
    public void retrofitClientTest() throws IOException {
        assertNotNull(github);

        Call<List<Github.Repo>> call = github.listRepos("Square");

        List<Github.Repo> body = call.execute().body();

        body.forEach(System.out::println);
    }

}

package cn.dysania.retrofit.sample.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import cn.dysania.retrofit.sample.client.Github;

/**
 * TODO 类描述
 *
 * @author liangtian
 */
@RestController
public class GithubController {

    @Autowired
    private Github github;

    @GetMapping("/fetchGithubRepo/{user}")
    public List<Github.Repo> fetchGithubRepo(@PathVariable String user){
        return github.returnHystrixCommandBody(user).execute();
    }
}

package com.ftibw.demo.service;

import com.ftibw.demo.entity.UserInfo;
import com.ftibw.demo.gateway.config.OAuth2FeignConfigure;
import com.ftibw.demo.service.hystrix.UserServiceHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @author : Ftibw
 * @date : 2019/6/17 17:20
 */
@FeignClient(
        // value == name == service-id == spring.application.name == service-ip:service-port
        name = "user-service",
        // path = [server.servlet.context-path] + [controller-RequestMapping]
        // 如果这里后缀加了controller-RequestMapping,那么对应service中controller上必须加RequestMapping
        // 这里后缀不加controller-RequestMapping,则在方法注解上加controller-RequestMapping作为前缀
        path = "/user-service",
        fallback = UserServiceHystrix.class,
        configuration = OAuth2FeignConfigure.class
)
public interface UserService {

    /**
     * method-RequestMapping
     */
    @PostMapping("/api/v1/users")
    List<UserInfo> listUsers();
}

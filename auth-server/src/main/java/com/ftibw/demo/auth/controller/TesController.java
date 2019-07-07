package com.ftibw.demo.auth.controller;

import com.ftibw.demo.entity.UserInfo;
import com.ftibw.demo.service.UserService;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : Ftibw
 * @date : 2019/6/17 17:59
 */
@RestController
public class TesController {

    private final UserService us;

    @Autowired
    public TesController(UserService us) {
        this.us = us;
    }

    /**
     * @see feign.SynchronousMethodHandler#executeAndDecode(RequestTemplate):
     * response = this.client.execute(request, this.options);
     */
    @GetMapping("/users")
    public List<UserInfo> listUsers() {
        //TODO oauth2 feign
        // 如果不做专门配置,feign发起的请求是不带token的,远程调用将会失败
        // (实际响应needLogin,却被解析成List失败,导致调用fallback)
        return us.listUsers();
    }
}

package com.ftibw.demo.auth.controller;

import com.ftibw.demo.auth.bean.MongoClientDetails;
import com.ftibw.demo.auth.config.WebSecurityConfig;
import com.ftibw.demo.auth.service.MongoClientDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Ftibw
 * @date : 2019/6/17 17:59
 */
@Api(description = "CustomAuthEndpoint")
@Slf4j
@RestController
public class AuthController {


    private final MongoClientDetailsService clientDetailsService;

    public AuthController(MongoClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    @GetMapping(WebSecurityConfig.NEED_LOGIN_URL)
    public Object needLogin() {
        Map<String, Object> ret = new HashMap<>(2);
        ret.put("code", 0);
        ret.put("msg", "Need Login!");
        return ret;
    }

    /**
     * 非常重要
     * client会从该接口读取信息校验
     * 见gateway的配置文件:
     * spring.security.oauth2.client.provider.q-media.user-info-uri
     */
    @GetMapping("/api/user")
    public Principal getUserInfo(Principal principal) {
        return principal;
    }

    @ApiOperation(value = "添加一个客户详情")
    @PostMapping("/api/client")
    public Object addClientDetails(@RequestBody MongoClientDetails clientDetails) {
        try {
            clientDetails.setClientSecret(clientDetailsService.getPasswordEncoder().encode(clientDetails.getClientSecret()));
            clientDetailsService.addClientDetails(clientDetails);
        } catch (ClientAlreadyExistsException e) {
            log.error("#### 添加客户失败", e);
            return "failed";
        }
        return "success";
    }

    @PostMapping("/api/client/{clientId}/scope/{scopeList}/autoapprove")
    public Object enableAutoApproveScopes(
            @PathVariable("clientId") String clientId,
            @PathVariable("scopeList") List<String> autoScopes) {

        if (null == clientId || null == autoScopes || autoScopes.isEmpty()) {
            return "nothing changed";
        }

        ClientDetails modified = clientDetailsService.updateAutoApproveScopesByClientId(clientId, autoScopes);
        List<String> modifiedSuccess = new ArrayList<>(autoScopes.size());
        for (String scope : autoScopes) {
            if (modified.isAutoApprove(scope)) {
                modifiedSuccess.add(scope);
            }
        }
        JSONObject ret = new JSONObject();
        ret.put("msg", "these scopes change to autoapprove success");
        ret.put("scopes", modifiedSuccess);
        return ret;
    }

    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("q-media"));
        System.out.println(passwordEncoder.encode("swagger2"));
    }
}

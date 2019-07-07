package com.ftibw.demo.gateway.controller;

import com.ftibw.demo.entity.UserInfo;
import com.ftibw.demo.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
public class OAuth2LoginController {

//    @Autowired
//    RestTemplate restTemplate;

    private final UserService us;

    public OAuth2LoginController(UserService us) {
        this.us = us;
    }

    @GetMapping("/")
    public String index(Model model,
                        @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
                        @AuthenticationPrincipal OAuth2User oauth2User) {
        model.addAttribute("userName", oauth2User.getName());
        model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientName());
        model.addAttribute("userAttributes", oauth2User.getAttributes());
        return "index";
    }

    @GetMapping("/tes")
    @ResponseBody
    public List<UserInfo> tes() {
        return us.listUsers();
//        String uri = "http://localhost:9100/user-service/api/vi/users";
//        ResponseEntity<List<UserInfo>> listEntity = restTemplate.exchange(uri, HttpMethod.POST, null, new ParameterizedTypeReference<List<UserInfo>>() {
//        });
//        return listEntity.getBody();
    }
}
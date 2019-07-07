package com.ftibw.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;

import java.util.Map;

/**
 * @author : Ftibw
 * @date : 2019/6/18 9:44
 * <p>
 * 配置文件规则见 {@link OAuth2ClientPropertiesRegistrationAdapter#getBuilder(String, String, Map)}
 * 默认内置了4大provider {@link CommonOAuth2Provider#{GOOGLE,GITHUB,FACEBOOK,OKTA}
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.ftibw.demo.service")
public class GatewayApplication {


//    @Bean
//    RestTemplate restTemplate(OAuth2AuthorizedClientService clientService) {
//        return new RestTemplateBuilder()
//                .interceptors((ClientHttpRequestInterceptor) (httpRequest, bytes, execution) -> {
//                    OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//                    OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId()
//                            , token.getName());
//
//                    httpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());
//                    return execution.execute(httpRequest, bytes);
//                }).build();
//    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

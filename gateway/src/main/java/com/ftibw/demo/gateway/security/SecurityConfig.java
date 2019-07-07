package com.ftibw.demo.gateway.security;

import com.ftibw.demo.gateway.jwt.JwtOAuth2AuthenticationTokenConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .logout()
                .logoutSuccessHandler(new RedirectServerLogoutSuccessHandler())
                .and()
                .authorizeExchange()
                .anyExchange()
                .authenticated()
                .and()
                .oauth2Login()
//                .and()
//                .formLogin()

                //client
//                .and()
//                .oauth2Client()

//                .and()
//                .exceptionHandling()
//                // 用于debug时查看原因(不配置自动跳转主页),身份认证失败处理器(登录失败,或者access_token无效),
//                .authenticationEntryPoint((exchange, e) -> {
//                    e.printStackTrace();
//                    return Mono.empty();
//                })
//                .accessDeniedHandler((serverWebExchange, e) -> {
//                    System.out.println(serverWebExchange.getRequest().getPath().pathWithinApplication().value());
//                    return Mono.empty();
//                })
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(new JwtOAuth2AuthenticationTokenConverter())
        ;
        return http.build();
    }

    /**
     * 功能定位等他HttpClient
     */
//    @Bean
//    WebClient webClient(ReactiveClientRegistrationRepository clientRegistrationRepository,
//                        ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
//
//        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
//                new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, authorizedClientRepository);
//        oauth.setDefaultOAuth2AuthorizedClient(true);
//        return WebClient.builder()
//                .filter(oauth)
//                .build();
//    }

}
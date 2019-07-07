package com.ftibw.demo.gateway.jwt;

import com.ftibw.demo.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerTokenServicesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;

/**
 * 这个ctrl+左键跳转链接 <a>https://github.com/artemMartynenko/spring-cloud-gateway-oauth2-sso-sample-application/tree/master/src/main/java/org/c4isr/delta/cloudgateway/jwt</a>
 */
@Configuration
public class JwtConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtConfiguration.class);

    @Value("${spring.security.oauth2.client.provider.q-media.jwk-set-uri}")
    private String jwtSetUri;
//    @Value("${spring.security.oauth2.client.registration.q-media.client-id}")
//    private String clientId;
//    @Value("${spring.security.oauth2.client.registration.q-media.client-secret}")
//    private String clientSecret;

    @Bean
    ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> userService(ReactiveJwtDecoder jwtDecoder) {
        return new JwtReactiveOAuth2UserService(jwtDecoder);
    }

    /**
     * 仅支持RSA
     * 加密方式:
     *
     * @see ResourceServerTokenServicesConfiguration.JwtTokenServicesConfiguration#getKeyFromServer()
     */
    @Bean
    ReactiveJwtDecoder jwtDecoder() throws IOException, InvalidKeyException {

        // 如过认证服务器将token_key(public key)访问路径权限开放了,就不需要basic认证了
        return WebClient.create()
                .get().uri(URI.create(jwtSetUri))
//                .header(
//                        HttpHeaders.AUTHORIZATION,
//                        TokenUtils.getClientBasicAuthorization(clientId, clientSecret)
//                )
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(JwtPublicKey.class))
                .map(jwtPublicKey -> TokenUtils.parsePublicKey(jwtPublicKey.getValue()))
                .map(NimbusReactiveJwtDecoder::new)
                .block();
    }
}

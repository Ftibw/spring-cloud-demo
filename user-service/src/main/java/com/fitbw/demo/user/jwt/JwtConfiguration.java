package com.fitbw.demo.user.jwt;

import com.ftibw.demo.utils.RestUtils;
import com.ftibw.demo.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoderJwkSupport;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JwtConfiguration {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwtSetUri;;

    @Bean
    OAuth2UserService<OAuth2UserRequest, OAuth2User> userService(JwtDecoder jwtDecoder) {
        return new JwtOAuth2UserService(jwtDecoder);
    }

    /**
     * 巨坑,内置的{@link NimbusJwtDecoderJwkSupport}解码token的时候,
     * json解析失败,因为json里面的key和认证服务器JwtAccessTokenConverter提供的不一致...
     */
    @Bean
    JwtDecoder jwtDecoder() {

        JwtPublicKey pk = RestUtils.getFormUrlEncoded(
                jwtSetUri,
                null,null,
//                httpHeaders -> httpHeaders.add(
//                        HttpHeaders.AUTHORIZATION,
//                        TokenUtils.getClientBasicAuthorization("q-media", "q-media")
//                ),
                new ParameterizedTypeReference<JwtPublicKey>() {
                },
                new RestTemplate()
        );

        final NimbusReactiveJwtDecoder jwtDecoder = new NimbusReactiveJwtDecoder(TokenUtils.parsePublicKey(pk.getValue()));
        return token -> jwtDecoder.decode(token).block();
    }
}

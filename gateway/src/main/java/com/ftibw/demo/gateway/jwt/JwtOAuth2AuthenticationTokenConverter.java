package com.ftibw.demo.gateway.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

public class JwtOAuth2AuthenticationTokenConverter implements Converter<Jwt, Mono<OAuth2AuthenticationToken>> {

    private Converter<Jwt, OAuth2User> jwtToOAuth2UserConverter = new JwtToOAuth2UserConverter();

    @Override
    public Mono<OAuth2AuthenticationToken> convert(Jwt jwt) {
        OAuth2User jwtOAuth2User = jwtToOAuth2UserConverter.convert(jwt);
        OAuth2AuthenticationToken auth2AuthenticationToken = new OAuth2AuthenticationToken(jwtOAuth2User, jwtOAuth2User.getAuthorities(), (String) jwt.getClaims().get("client_id"));
        return Mono.just(auth2AuthenticationToken);
    }

}

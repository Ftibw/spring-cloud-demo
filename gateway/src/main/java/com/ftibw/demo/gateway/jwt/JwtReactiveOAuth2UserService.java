package com.ftibw.demo.gateway.jwt;


import com.nimbusds.jwt.JWTParser;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

public class JwtReactiveOAuth2UserService implements ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final ReactiveJwtDecoder jwtDecoder;
    private final Converter<Jwt, OAuth2User> jwtToUserConverter = new JwtToOAuth2UserConverter();
    private static final DefaultReactiveOAuth2UserService defaultOAuth2UserService = new DefaultReactiveOAuth2UserService();

    public JwtReactiveOAuth2UserService(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * invoke by {@link OAuth2LoginReactiveAuthenticationManager#onSuccess(OAuth2AuthorizationCodeAuthenticationToken)}
     * 可通过{@link JWTParser#parse(String)}来验证是否是jwt-token
     */
    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String tokenValue = userRequest.getAccessToken().getTokenValue();
        return 2 == StringUtils.countMatches(tokenValue, ".")
                ? jwtDecoder.decode(tokenValue).map(jwtToUserConverter::convert)
                : defaultOAuth2UserService.loadUser(userRequest);
    }

}

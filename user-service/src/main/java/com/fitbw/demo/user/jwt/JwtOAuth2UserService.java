package com.fitbw.demo.user.jwt;


import com.nimbusds.jwt.JWTParser;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

public class JwtOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final JwtDecoder jwtDecoder;
    private final Converter<Jwt, OAuth2User> jwtToUserConverter = new JwtToOAuth2UserConverter();

    private static final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    public JwtOAuth2UserService(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * invoke by {@link OAuth2LoginReactiveAuthenticationManager#onSuccess(OAuth2AuthorizationCodeAuthenticationToken)}
     * 可通过{@link JWTParser#parse(String)}来验证是否是jwt-token
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String tokenValue = userRequest.getAccessToken().getTokenValue();
        return 2 == StringUtils.countMatches(tokenValue, ".")
                ? jwtToUserConverter.convert(jwtDecoder.decode(tokenValue))
                : defaultOAuth2UserService.loadUser(userRequest);
    }

}

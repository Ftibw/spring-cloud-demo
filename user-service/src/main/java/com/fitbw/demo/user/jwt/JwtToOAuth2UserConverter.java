package com.fitbw.demo.user.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtToOAuth2UserConverter implements Converter<Jwt, OAuth2User> {


    /**
     * user_name来自
     * {@link DefaultUserAuthenticationConverter#convertUserAuthentication(Authentication)}
     * response.put("user_name", authentication.getName());
     */
    @Override
    public OAuth2User convert(Jwt jwt) {
        return new JwtOAuth2User(toGrantedAuthorities(jwt.getClaims()), jwt.getClaims(), "user_name", jwt.getTokenValue());
    }

    private Collection<? extends GrantedAuthority> toGrantedAuthorities(Map<String, Object> claims) {
        Object authorities = claims.get("authorities");
        if (authorities instanceof Collection
                && String.class == CollectionUtils.findCommonElementType((Collection) authorities)) {
            return ((Collection<String>) authorities)
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return null;
    }
}

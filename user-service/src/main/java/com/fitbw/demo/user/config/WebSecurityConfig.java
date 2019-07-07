package com.fitbw.demo.user.config;

import com.fitbw.demo.user.jwt.JwtOAuth2AuthenticationTokenConverter;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


/**
 * 参考官方demo
 * <a>https://github.com/spring-projects/spring-security/tree/5.1.5.RELEASE/samples/boot/oauth2resourceserver</a>
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final static String GATEWAY_DOMAIN = "http://localhost:8100";
    private final static String RESP_CONTENT_TYPE = "application/json;charset=utf-8";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .logout()
                // 统一从网关登出
                .logoutSuccessUrl(GATEWAY_DOMAIN + "/logout")
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                /*
                 权限不足配置(登录成功状态下)
                */
                .and()
                .exceptionHandling()
                // 身份认证失败处理器(access_token无效)
                .authenticationEntryPoint((req, resp, e) -> {
                    JSONObject ret = new JSONObject();
                    ret.put("code", 0);
                    ret.put("msg", e.getMessage());
                    resp.setContentType(RESP_CONTENT_TYPE);
                    resp.getWriter().write(ret.toString());
                })
                // 访问拒绝处理器(认证成功,但是权限不足)
                .accessDeniedHandler((req, resp, auth) -> {
                    JSONObject ret = new JSONObject();
                    ret.put("code", 0);
                    ret.put("msg", "权限不足，请联系管理员!");
                    resp.setContentType(RESP_CONTENT_TYPE);
                    resp.getWriter().write(ret.toString());
                })
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(new JwtOAuth2AuthenticationTokenConverter())

        //        .and()
//                .addFilterBefore((req, resp, chain) -> {
//                    if (req instanceof HttpServletRequest) {
//                        String authorization = ((HttpServletRequest) req).getHeader(AUTHORIZATION_HEADER);
//                        if (StringUtils.isNotBlank(authorization)) {
//                            String tokenType = authorization.substring(0, 6);
//                            String tokenValue = authorization.substring(7);
//                            OAuth2Authentication authentication = tokenServices.loadAuthentication(tokenValue);
//                            authentication.setAuthenticated(true);
//
//                            req.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, tokenValue);
//                            req.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, tokenType);
//                            authentication.setDetails(authenticationDetailsSource.buildDetails((HttpServletRequest) req));
//                            SecurityContextHolder.getContext().setAuthentication(authentication);
//                        }
//                    }
//                    chain.doFilter(req, resp);
//                }, RequestCacheAwareFilter.class)
        ;

    }
}


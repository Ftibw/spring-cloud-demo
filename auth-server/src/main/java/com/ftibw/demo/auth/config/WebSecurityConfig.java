package com.ftibw.demo.auth.config;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 前后端分离配置 spring security
 *
 * @author ftibw
 * @date 2019/6/30
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String NEED_LOGIN_URL = "/sso/needLogin";

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public WebSecurityConfig(
            @Qualifier("customUserDetailsService") UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

//    @Bean
//    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
//        StrictHttpFirewall firewall = new StrictHttpFirewall();
//        //允许 uri中出现"//"
//        firewall.setAllowUrlEncodedSlash(true);
//        return firewall;
//    }

    //TODO uri 双斜杠报错暂时没有解决
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
//    }

    /**
     * 通过HttpSecurity提供的链式方法
     * <p>
     * New AbstractHttpConfigurer实现类
     * 然后getOrApply(configurer) --- 存在则返回存在的,不存在则添加新New的
     * <p>
     * 最终HttpSecurity所有configurer
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // uri : '/login'
        // method : 'POST'
        // formData : {username:'account',password:'md5(pwd)')
        final String formLoginUrl = "/login";

        http
                /*
                 开启跨域
                */
                .csrf().disable() //.cors().and() 默认就有
                /*
                 默认为SessionCreationPolicy.NEVER,这个策略感觉没什么用
                 使用restful风格,请求无状态,没有session---TODO 目前还没实现无session化
                */
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()

                /*
                 登录配置
                */
                // /login自动有permitAll权限
                .formLogin()
                // 前后端分离时,未登录的重定向,一定不能取默认值/login,且非默认值时需要开放访问权限
                // 否则未登录就会使用默认值/login重定向
                // 默认值/login时,跳转页面优先级 高于 访问自定义接口
//                .loginPage(NEED_LOGIN_URL)

                // 登录页面POST请求登录的地址,不能为空!!!
                // 可以取/开头的任意值,自动permitAll权限
                .loginProcessingUrl(formLoginUrl)
                //登录成功处理器 需要自己在认证成功后jwt-token设置到响应中
                .successHandler((req, resp, authentication) -> {

                    JSONObject ret = new JSONObject();
                    ret.put("code", 1);
                    ret.put("msg", "Login Success!");
                    Map<String, Object> data = new HashMap<>(2);
                    UserDetails user = (UserDetails) authentication.getPrincipal();
                    List<String> auth = user.getAuthorities()
                            .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
                    data.put("account", user.getUsername());
                    data.put("auth", auth);

                    ret.put("data", data);
                    resp.setCharacterEncoding("UTF-8");
                    resp.getWriter().write(ret.toString());
                })
                //登录失败处理器
                .failureHandler((req, resp, e) -> {
                    String msg;
                    if (e instanceof BadCredentialsException ||
                            e instanceof UsernameNotFoundException) {
                        msg = "账户名或者密码输入错误!";
                    } else if (e instanceof LockedException) {
                        msg = "账户被锁定，请联系管理员!";
                    } else if (e instanceof CredentialsExpiredException) {
                        msg = "密码过期，请联系管理员!";
                    } else if (e instanceof AccountExpiredException) {
                        msg = "账户过期，请联系管理员!";
                    } else if (e instanceof DisabledException) {
                        msg = "账户被禁用，请联系管理员!";
                    } else {
                        msg = "登录异常!";
                    }
                    JSONObject ret = new JSONObject();
                    ret.put("code", 0);
                    ret.put("msg", msg);

                    resp.setStatus(401);
                    resp.setCharacterEncoding("UTF-8");
                    resp.getWriter().write(ret.toString());
                })

                /*
                 登出配置
                */
                .and()
                .logout()
                // logoutUrl缺省时默认值/logout,自动permitAll权限,仅为当前应用登出
//                .logoutUrl("/logout")

                // logoutSuccessUrl回调一个自定义接口,处理登出成功事件
//                .logoutSuccessUrl("/sso/logout")

                // 不配做logoutSuccessHandler时,默认使用loginPage重定向
                .logoutSuccessHandler((req, resp, auth) -> {

                    if (null == auth) {
                        resp.sendRedirect(NEED_LOGIN_URL);
                        return;
                    }

                    JSONObject ret = new JSONObject();

//                    if (auth.isAuthenticated()) {
//                        ssoLogout(req, ret);
//                    } else {
//                        ret.put("code", 0);
//                        ret.put("msg", "权限不足，请联系管理员!");
//                    }

                    ret.put("code", 1);
                    ret.put("msg", "注销成功!");
                    resp.setContentType("application/json;charset=utf-8");
                    resp.getWriter().write(ret.toString());
                })

                /*
                 未登录重定向路径及其权限配置
                */
                .and()
                .authorizeRequests()
                .antMatchers(NEED_LOGIN_URL,
                        "/webjars/**",
                        "/resources/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/v2/api-docs").permitAll()
                .anyRequest().authenticated()

                /*
                 认证或授权异常处理器
                */
//                .and()
//                .exceptionHandling()
//                // 不进行配置,会自动重定向needLogin , debug 时可配置
//                .accessDeniedHandler((req, resp, e) -> e.printStackTrace())
//                .authenticationEntryPoint((req, resp, e) -> e.printStackTrace())
        ;
    }

//    private void ssoLogout(HttpServletRequest req, JSONObject ret) {
//        // 远程调用进行client登出需要带上token,没有token即使响应正常也是登出无效
//        Consumer<HttpHeaders> headerSetter = header -> header.set("headName", "headValue");
//        //从ClientDetails中读取
//        Set<String> subSystemLogoutUrls = new HashSet<>(Arrays.asList(
//                "http://localhost:9100/user-service/logout",
//                "http://localhost:9200/program-service/logout"
//        ));
//
//        boolean allSuccess = true;
//        List<String> logoutFailedServices = new ArrayList<>(subSystemLogoutUrls.size() - 1);
//
//        String referer = req.getHeader("referer");
//        for (String subSystemUrl : subSystemLogoutUrls) {
//            //来自子系统登出的回调,则子系统已经登出不用再远程登出了
//            if (subSystemUrl.equals(referer)) {
//                continue;
//            }
//            try {
//                AuthApplication.postFormUrlEncoded(subSystemUrl, null, headerSetter, new ParameterizedTypeReference<String>() {
//                }, this.restTemplate);
//                // spring session redis 是分布式session,单独存储在redis中
//                // 但是每个子系统的权限只能通过/logout进行清除(目前权限使用的jwt-token,没有存储在局部)
//            } catch (Exception e) {
//                allSuccess = false;
//                String uri = subSystemUrl.substring(0, subSystemUrl.lastIndexOf("/"));
//                logoutFailedServices.add(uri.substring(uri.lastIndexOf("/") + 1));
//            }
//        }
//
//        if (allSuccess) {
//            ret.put("code", 1);
//            ret.put("msg", "注销成功!");
//        } else {
//            ret.put("code", 0);
//            ret.put("msg", "注销异常!");
//            ret.put("logoutFailedServices", logoutFailedServices);
//        }
//    }

}

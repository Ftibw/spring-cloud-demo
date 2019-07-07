package com.ftibw.demo.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * TODO 跨域配置不生效,用Nginx算了
 * CorsRegistry的方式无法实现RouteFunctions配置的路由跨域，
 * 而CorsWebFilter的方式只是单纯的拦截请求，其他框架层的代码无法读取到跨域的配置
 * 比如说RequestMappingHandlerMapping#getHandler时就无法读取到跨域配置，可以考虑两者都配置
 */
@Configuration
public class CorsConfig /*implements WebFluxConfigurer*/ {

    /**
     * 全局跨域配置，根据各自需求定义
     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowCredentials(true)
//                .allowedOrigins("*")
//                .allowedHeaders("*")
//                .allowedMethods("*")
//                .exposedHeaders(HttpHeaders.SET_COOKIE);
//    }

    /**
     * 正确的解决跨域问题的方法时使用CorsFilter过滤器
     */
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }


}

//package com.ftibw.demo.auth.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.core.env.Profiles;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * @author ftibw
// */
//@Configuration
//public class WebConf implements WebMvcConfigurer {
//
//    private final Environment environment;
//
//    public WebConf(Environment environment) {
//        this.environment = environment;
//    }
//
//    /**
//     * 非生产环境启用swagger2生成接口文档
//     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        if (environment.acceptsProfiles(Profiles.of("pro"))) {
//            return;
//        }
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }
//}
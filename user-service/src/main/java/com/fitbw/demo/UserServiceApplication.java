package com.fitbw.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * @author : Ftibw
 * @date : 2019/6/17 15:36
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.ftibw.demo.service")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}

package com.ftibw.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author : Ftibw
 * @date : 2019/6/17 18:03
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.ftibw.demo.service")
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}

package com.fitbw.demo.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class TesController {
 
    @Value("${foo}")
    String foo;

    @PreAuthorize("hasAuthority('3')")
    @GetMapping("/foo")
    public String hi(){
        return foo;
    }

    @GetMapping("/xxx")
    public String callback(){
        return foo;
    }
}
package com.ftibw.demo.service.hystrix;

import com.ftibw.demo.entity.UserInfo;
import com.ftibw.demo.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author : Ftibw
 * @date : 2019/6/17 17:26
 */
@Component
public class UserServiceHystrix implements UserService {
    @Override
    public List<UserInfo> listUsers() {
        return Collections.singletonList(UserInfo.builder().account("hystrix what fuck").build());
    }
}

package com.fitbw.demo.user.service.impl;

import com.ftibw.demo.entity.UserInfo;
import com.ftibw.demo.service.UserService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Ftibw
 * @date : 2019/6/17 17:51
 */
@CrossOrigin
@RestController
@RefreshScope
public class UserServiceImpl implements UserService {

//    @PreAuthorize("hasAuthority('5')")
    @Override
    public List<UserInfo> listUsers() {
        int len = 5;
        List<UserInfo> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            list.add(UserInfo.builder()
                    .account("kmsz_zxw" + i)
                    .name("ftibw" + i)
                    .telephone("15927544743")
                    .build());
        }
        return list;
    }
}

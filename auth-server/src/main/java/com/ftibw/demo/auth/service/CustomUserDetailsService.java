package com.ftibw.demo.auth.service;

import com.ftibw.demo.auth.bean.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : Ftibw
 * @date : 2019/7/6 2:16
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

//    @Autowired
//    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //find securityUser from database
        String password = "123456";

        if (null == password || password.trim().length() == 0) {
            log.warn("用户{}不存在", username);
            throw new UsernameNotFoundException(username);
        }

        List<String> permissionList = Arrays.asList("0", "1", "2");
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(permissionList)) {
            for (String permission : permissionList) {
                authorityList.add(new SimpleGrantedAuthority(permission));
            }
        }

        return new CustomUser(username, passwordEncoder.encode(password), authorityList);
    }
}

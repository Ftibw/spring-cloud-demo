package com.ftibw.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Ftibw
 * @date : 2019/6/17 17:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
    private String account;
    private String password;
    private String name;
    private String age;
    private String address;
    private String telephone;
}

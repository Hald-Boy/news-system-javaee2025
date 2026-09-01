package com.guat.mynewsapp.dto;

import lombok.Data;

@Data
public class SmsSendDTO {
    private String phone;//手机号
    private Integer scene;//验证码使用场景 1注册 2登录
}

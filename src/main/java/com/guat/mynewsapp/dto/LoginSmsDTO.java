package com.guat.mynewsapp.dto;

import lombok.Data;

@Data
public class LoginSmsDTO {
    private String phone;
    private String smsCode;
}

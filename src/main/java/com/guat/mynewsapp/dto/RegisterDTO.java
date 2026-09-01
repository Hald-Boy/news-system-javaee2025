package com.guat.mynewsapp.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String phone;
    private String smsCode;
    private String passWord;
}

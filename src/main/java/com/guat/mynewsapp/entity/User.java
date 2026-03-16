package com.guat.mynewsapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id; //用户id
    private String username;    //用户名
    private String password;    //用户密码
    private int role;   //用户的身份（0代表用户，1代表管理员，默认是0）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public LocalDateTime createTime;    //用户的注册时间
}

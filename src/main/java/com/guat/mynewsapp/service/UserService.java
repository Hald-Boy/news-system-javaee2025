package com.guat.mynewsapp.service;
import com.guat.mynewsapp.entity.News;
import com.guat.mynewsapp.entity.NewsLike;
import com.guat.mynewsapp.entity.PageBean;
import com.guat.mynewsapp.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserService {

    PageBean getAllUsers(String username, Integer role, LocalDate createTime, Integer page, Integer pageSize);


    /**
     * 登录校验
     * @param user 传进来的用户名和密码封装在Emp对象
     * @return 返回一个员工
     */
    User login(User user);

    /**
     * 用户注册
     * @param user 封装用户名和密码
     */
    void addUser(User user);

    /**
     * 用户信息修改
     */
    void updateUser(User user);

    /**
     * 用户信息查询
     * @param id 根据ID来
     * @return 返回一个用户
     */
    User getUserById(Integer id);


    void deleteUserById(Integer id);


    void updateUserById(User user);


    /** 手机号 + 短信验证码注册，注册后生成随机昵称/账号 */
    User register(String phone, String smsCode, String password);

    /** 手机号 + 密码登录 */
    User loginByPassword(String phone, String password);

    /** 手机号 + 短信验证码登录（仅限已注册手机号） */
    User loginBySms(String phone, String smsCode);

    User getById(Integer id);


}

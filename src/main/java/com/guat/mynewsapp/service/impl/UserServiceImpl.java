package com.guat.mynewsapp.service.impl;

import com.guat.mynewsapp.entity.News;
import com.guat.mynewsapp.entity.PageBean;
import com.guat.mynewsapp.entity.User;
import com.guat.mynewsapp.mapper.UserMapper;
import com.guat.mynewsapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    public PageBean getAllUsers(String username, Integer role, LocalDate createTime,Integer page,Integer pageSize) {


        //获取分页查询的起始索引
        int start = (page - 1)*page;
        PageBean pageBean = new PageBean();

        LocalDateTime startTime = null; // 当日00:00:00
        LocalDateTime endTime = null;   // 当日23:59:59
        if(createTime != null) {
            startTime = createTime.atStartOfDay();// 转换为：2025-12-04 00:00:00
            endTime = createTime.atTime(23, 59, 59); // 转换为：2025-12-04 23:59:59
        }

        //调用Mapper接口的方法
        pageBean.setRows(userMapper.selectUsers(username,role,createTime,start,pageSize,startTime,endTime)); //用户列表
        pageBean.setTotal(userMapper.countUsers()); //总记录数
        return pageBean;
    }



    /**
     * 登录
     * @param user 传进来的用户名和密码封装在Emp对象
     * @return .
     */
    @Override
    public User login(User user){
        return userMapper.selectLogin(user);
    }

    /**
     * 注册
     * @param user 封装用户信息
     */
    @Override
    public void addUser(User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setRole(0);
        userMapper.addUser(user);
    }

    /**
     * 修改用户信息
     * @param user 封装修改的数据
     */
    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    /**
     * 查询用户信息
     * 查询回显，从请求头获取id
     * @param id 根据ID查询
     * @return 返回一条记录
     */
    @Override
    public User getUserById(Integer id) {
        return userMapper.getUserById(id);
    }

    /**
     * 注销账号（删除）
     * @param id 要删除的用户的ID
     * @return 不用返回zhi
     */
    @Override
    public void deleteUserById(Integer id) {
        userMapper.deleteUserById(id);
    }


    /**
     * 修改自己的信息
     * @param user .
     */
    @Override
    public void updateUserById(User user) {
        userMapper.updateUserById(user);
    }


//    /**
//     * //管理员修改信息
//     * @param user
//     */
//    @Override
//    public void adminUpdateUser(User user) {
//
//    }

//    /**
//     * 权限分配接口
//     * @param userId .
//     * @param role .
//     */
//    @Override
//    public void assignPermission(Integer userId, Integer role) {
//        userMapper.assignPermission(userId, role);
//    }

}

package com.guat.mynewsapp.mapper;

import com.guat.mynewsapp.entity.News;
import com.guat.mynewsapp.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {


    /**
     * 查询所有用户
     * @return 返回所有用户列表
     */
    @Select("select count(*) from user")
    Long countUsers();
    List<News> selectUsers(String username, Integer role, LocalDate createTime, Integer start, Integer size,LocalDateTime startTime, LocalDateTime endTime);




    /**
     * 登录校验：查询有没有和输入的用户名和密码匹配的员工记录
     * @param user 传进来的用户名和密码封装在Emp对象里
     * @return 返回一个员工，如果emp为空，说明用户名或密码错误，反之则成功
     */
    @Select("select * from user where username = #{username} and password = #{password}")
    User selectLogin(User user);

//    /**
//     * 注册
//     */
    @Insert("insert into user (username, password, role, create_time) values (#{username},#{password},#{role},#{createTime})")
    void addUser(User user);

    /**
     * 修改用户信息
     * @param user .
     */
    //@Update("update user set username = #{username}, password = #{password}, role = #{role} where id = #{id}")
    void updateUser(User user);

    /**
     * 用户信息查询
     * @param id 根据ID来
     * @return 返回的数据装在User
     */
    @Select("select * from user where id = #{id}")
    User getUserById(Integer id);


    @Delete("delete from user where id = #{id}")
    void deleteUserById(Integer id);

    //修改自己信息
    void updateUserById(User user);

//    /**
//     * 权限分配接口
//     * @param userId
//     * @param role
//     */
//    @Update("update user set role = #{role} where id = #{userId}")
//    void assignPermission(Integer userId, Integer role);

    //void adminUpdateUser(User user);

}

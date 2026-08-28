package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.entity.PageBean;
import com.guat.mynewsapp.entity.Result;
import com.guat.mynewsapp.entity.User;
import com.guat.mynewsapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 模块标签（Swagger UI 分类）
@Tag(name = "用户管理接口", description = "提供用户分页查询、信息修改、个人信息查询、注销、信息更新等功能，部分接口需携带Token授权")
@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 2025/10/15 于APIPost测试成功
     * @param username 模糊匹配查询
     * @param role 身份
     * @param createTime 创建时间
     * @param page 页码
     * @param pageSize 记录数
     * @return 返回总记录数和列表
     */
    @Operation(
            summary = "分页查询用户列表",
            description = "支持按用户名模糊查询、角色筛选、创建时间筛选，默认页码1、每页10条数据"
    )
    @Parameters({
            @Parameter(name = "username", description = "用户名（模糊匹配），非必填", required = false, example = "张三"),
            @Parameter(name = "role", description = "用户角色（如1=管理员，0=普通用户），非必填", required = false, example = "0"),
            @Parameter(name = "createTime", description = "用户创建日期（格式：yyyy-MM-dd），非必填", required = false, example = "2025-12-01"),
            @Parameter(name = "page", description = "页码，默认值1", required = false, example = "1"),
            @Parameter(name = "pageSize", description = "每页记录数，默认值10", required = false, example = "10")
    })
    @GetMapping("/users/list")
    public Result getAllUsers(
            String username, //关键字模糊查询
            Integer role,//身份
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createTime, //指定前端传过来的参数是yyyy-MM-dd
            @RequestParam(defaultValue = "1") Integer page,//设置默认值，下同
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        log.info("username:{},role:{},createTime:{},page:{},pageSize:{}", username, role, createTime, page, pageSize);
        //调用service查询新闻数据
        PageBean pageBean = userService.getAllUsers(username,role,createTime,page,pageSize);
        return Result.success(pageBean);
    }

    /**
     * 用户信息修改
     * 2025/10/16    只能修改role之外的数据
     * @param user 封装修改的数据
     * @return 返回提示信息
     */
    @Operation(
            summary = "管理员修改其他用户信息",
            description = "用户id从前端传入"
    )
    @Parameters({
            @Parameter(name = "user", description = "用户修改信息（JSON格式），仅支持role外的字段（如用户名、昵称等）", required = true)
    })
    @PostMapping("/user/update")
    public Result updateUser(@RequestBody User user) {
        //要修改的用户的id由前端传入
        userService.updateUser(user);
        return Result.success("用户信息修改成功！");
    }



    /**
     * 2025/10/10
     * 切换到「Headers」标签页，添加授权请求头 Authorization，Bearer Token值
     * 用户信息查询
     * 根据ID查询，用于查询回显和渲染个人信息
     * @return 返回查询到的记录给前端
     */
    @Operation(
            summary = "查询当前登录用户信息",
            description = "需在请求头添加Authorization: Bearer {Token}；返回结果会隐藏密码字段，userId从请求域获取"
    )
    @Parameter(name = "request", description = "请求对象（内含拦截器存入的userId），无需前端传参", hidden = true)
    @GetMapping("/user/current")
    public Result getUserById(HttpServletRequest request) {
        try {
            //从请求域获取拦截器存入的userId
            Integer userId = (Integer) request.getAttribute("userId");
            //根据userId查询当前登录用户的信息
            User user = userService.getUserById(userId);
            //隐藏密码
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            log.error("查询当前用户信息失败！",e);
            return Result.error("获取用户信息失败！");
        }
    }


    /**
     * 2025/10/10
     * 切换到「Headers」标签页，添加授权请求头 Authorization，Bearer Token值
     * 注销
     * 根据ID查询
     */
    @Operation(
            summary = "注销当前登录用户",
            description = "需在请求头添加Authorization: Bearer {Token}；删除当前用户账号，userId从请求域获取"
    )
    @Parameter(name = "request", description = "请求对象（内含拦截器存入的userId），无需前端传参", hidden = true)
    @GetMapping("/user/deleteCurrent")
    public Result deleteUserById(HttpServletRequest request) {
        try {
            //从请求域获取拦截器存入的userId
            Integer userId = (Integer) request.getAttribute("userId");
            //根据userId查询当前登录用户的信息
            userService.deleteUserById(userId);
            //隐藏密码
            //user.setPassword(null);
            //return Result.success(user);
            return Result.success("注销成功！");
        } catch (Exception e) {
            log.error("注销失败！",e);
            return Result.error("注销失败！");
        }
    }


    @Operation(
            summary = "更新当前登录用户基础信息",
            description = "支持修改用户名、密码、创建时间；创建时间格式为yyyy-MM-dd HH:mm:ss；需携带Token授权，userId从请求域获取"
    )
    @Parameters({
            @Parameter(name = "request", description = "请求对象（内含拦截器存入的userId），无需前端传参", hidden = true),
            @Parameter(name = "username", description = "新用户名，非必填", required = false, example = "李四"),
            @Parameter(name = "password", description = "新密码，非必填", required = false, example = "123456"),
            @Parameter(name = "createTime", description = "创建时间（格式：yyyy-MM-dd HH:mm:ss），非必填", required = false, example = "2025-12-01 10:00:00")
    })
    @PostMapping("user/updateCurrent")
    public Result updateCurrentUser(
                                     HttpServletRequest request,
                                     @RequestParam(value = "username", required = false) String username,
                                     @RequestParam(value = "password", required = false) String password,
                                     @RequestParam(value = "createTime", required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String createTimeStr
    ){
        // 手动解析（格式和ApiPost传的完全一致）
        LocalDateTime createTime = null;
        if (createTimeStr != null && !createTimeStr.isEmpty()) {
            // 注意：这里的空格、冒号都是英文半角！
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            createTime = LocalDateTime.parse(createTimeStr, formatter);
        }

        User user = new User();
        //从请求域获取拦截器存入的userId
        Integer userId = (Integer) request.getAttribute("userId");
        //设置
        user.setId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setCreateTime(createTime);
        log.info("username:{},password:{},createTime:{}",username,password,createTime);
        userService.updateUserById(user);
        return Result.success("修改成功！");
    }

}